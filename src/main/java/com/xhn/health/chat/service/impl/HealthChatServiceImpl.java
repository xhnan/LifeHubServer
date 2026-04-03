package com.xhn.health.chat.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xhn.health.chat.model.AgentErrorEvent;
import com.xhn.health.chat.model.AgentFinalEvent;
import com.xhn.health.chat.model.AgentNodeEvent;
import com.xhn.health.chat.model.AgentStartEvent;
import com.xhn.health.chat.model.HealthChatStreamChunk;
import com.xhn.health.chat.model.HealthChatStreamRequest;
import com.xhn.health.chat.model.PythonAgentRequest;
import com.xhn.health.chat.service.HealthChatService;
import com.xhn.health.chat.service.PythonAgentService;
import com.xhn.health.psychology.chatmemories.model.HealthPsyChatMemories;
import com.xhn.health.psychology.chatmemories.service.HealthPsyChatMemoriesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthChatServiceImpl implements HealthChatService {

    private static final String DEFAULT_SYSTEM_PROMPT =
            "You are the LifeHub health assistant.\n"
                    + "Provide concise and practical health guidance with supportive tone.\n"
                    + "Do not fabricate user data. If the topic involves diagnosis, medication, or emergency risk, ask the user to contact a medical professional.";

    private final DeepSeekChatModel chatModel;
    private final HealthPsyChatMemoriesService chatMemoriesService;
    private final PythonAgentService pythonAgentService;
    private final ObjectMapper objectMapper;

    @Override
    public Flux<ServerSentEvent<HealthChatStreamChunk>> streamChat(Long userId, HealthChatStreamRequest request) {
        if (Boolean.TRUE.equals(request.getUseAgent())) {
            return streamChatWithAgent(userId, request);
        }
        return streamChatWithDeepSeek(userId, request);
    }

    private Flux<ServerSentEvent<HealthChatStreamChunk>> streamChatWithAgent(Long userId, HealthChatStreamRequest request) {
        return Flux.defer(() -> {
            saveChatMemory(userId, "user", request.getMessage());

            PythonAgentRequest agentRequest = PythonAgentRequest.builder()
                    .message(request.getMessage())
                    .user_id(request.getUserIdForAgent() != null ? request.getUserIdForAgent() : String.valueOf(userId))
                    .build();

            log.info("Calling Python Agent for userId={}", userId);

            return pythonAgentService.callAgentStream(agentRequest)
                    .doOnNext(sse -> log.info("Agent SSE inbound: event={}, data={}", sse.event(), abbreviate(sse.data())))
                    .flatMap(sse -> {
                        String event = sse.event();
                        String data = sse.data();

                        if (data == null || data.isEmpty()) {
                            return Flux.empty();
                        }

                        return processAgentEvent(event, data, userId);
                    })
                    .doOnNext(chunk -> log.info(
                            "Frontend chunk outbound(agent): type={}, done={}, content={}",
                            chunk.event(),
                            chunk.data() != null ? chunk.data().getDone() : null,
                            abbreviate(chunk.data() != null ? chunk.data().getContent() : null)
                    ))
                    .onErrorResume(error -> {
                        log.error("Python Agent streaming failed, userId={}", userId, error);
                        return Flux.just(event("error", "agent stream failed: " + error.getMessage(), true));
                    });
        });
    }

    private Flux<ServerSentEvent<HealthChatStreamChunk>> processAgentEvent(String eventType, String data, Long userId) {
        try {
            switch (eventType) {
                case "start":
                    objectMapper.readValue(data, AgentStartEvent.class);
                    return Flux.just(event("start", "", false));

                case "node":
                    AgentNodeEvent nodeEvent = objectMapper.readValue(data, AgentNodeEvent.class);
                    String nodeContent = formatNodeEvent(nodeEvent);
                    if (nodeContent != null) {
                        return Flux.just(event("delta", nodeContent, false));
                    }
                    return Flux.empty();

                case "final":
                    AgentFinalEvent finalEvent = objectMapper.readValue(data, AgentFinalEvent.class);
                    String finalResponse = extractFinalResponse(finalEvent);
                    if (finalResponse != null) {
                        saveChatMemory(userId, "assistant", finalResponse);
                        return Flux.just(
                                event("delta", finalResponse, false),
                                event("complete", "", true)
                        );
                    }
                    return Flux.just(event("complete", "", true));

                case "done":
                    return Flux.just(event("complete", "", true));

                case "error":
                    AgentErrorEvent errorEvent = objectMapper.readValue(data, AgentErrorEvent.class);
                    return Flux.just(event("error", "Agent error: " + errorEvent.getMessage(), true));

                default:
                    log.debug("Unknown agent event type: {}", eventType);
                    return Flux.empty();
            }
        } catch (Exception e) {
            log.error("Failed to process agent event: eventType={}, data={}", eventType, data, e);
            return Flux.just(event("error", "Failed to process event: " + e.getMessage(), true));
        }
    }

    private String formatNodeEvent(AgentNodeEvent nodeEvent) {
        String node = nodeEvent.getNode();
        if (node.contains("intent") || node.contains("tool") || node.contains("memory")
                || node.contains("assessment") || node.contains("review") || node.contains("record")) {
            return null;
        }
        return String.format("[agent node: %s]", node);
    }

    private String extractFinalResponse(AgentFinalEvent finalEvent) {
        if (finalEvent.getState() != null) {
            Object finalResponse = finalEvent.getState().get("final_response");
            if (finalResponse instanceof String value) {
                return value;
            }
        }
        return null;
    }

    private Flux<ServerSentEvent<HealthChatStreamChunk>> streamChatWithDeepSeek(Long userId, HealthChatStreamRequest request) {
        return Flux.defer(() -> {
            List<HealthPsyChatMemories> history = loadHistory(userId, request.getHistoryLimit());
            saveChatMemory(userId, "user", request.getMessage());

            List<Message> messages = buildMessages(history, request);
            StringBuilder assistantReply = new StringBuilder();

            Flux<ServerSentEvent<HealthChatStreamChunk>> aiStream = chatModel.stream(new Prompt(messages))
                    .flatMap(response -> {
                        String delta = extractContent(response);
                        if (delta == null || delta.isEmpty()) {
                            return Flux.empty();
                        }
                        assistantReply.append(delta);
                        return Flux.just(event("delta", delta, false));
                    })
                    .concatWith(Mono.fromCallable(() -> {
                        String reply = assistantReply.toString();
                        if (!reply.isBlank()) {
                            saveChatMemory(userId, "assistant", reply);
                        }
                        return event("complete", reply, true);
                    }))
                    .doOnNext(chunk -> log.info(
                            "Frontend chunk outbound(deepseek): type={}, done={}, content={}",
                            chunk.event(),
                            chunk.data() != null ? chunk.data().getDone() : null,
                            abbreviate(chunk.data() != null ? chunk.data().getContent() : null)
                    ))
                    .onErrorResume(error -> {
                        log.error("Health chat streaming failed, userId={}", userId, error);
                        return Flux.just(event("error", "stream failed: " + error.getMessage(), true));
                    });

            return Flux.concat(
                    Flux.just(event("start", "", false)),
                    aiStream
            );
        });
    }

    private List<HealthPsyChatMemories> loadHistory(Long userId, Integer historyLimit) {
        int limit = historyLimit == null ? 10 : historyLimit;
        if (limit <= 0) {
            return List.of();
        }
        return chatMemoriesService.getRecentChatMemoriesByUserId(userId, limit).stream()
                .sorted(Comparator.comparing(HealthPsyChatMemories::getCreatedAt, Comparator.nullsLast(LocalDateTime::compareTo)))
                .toList();
    }

    private List<Message> buildMessages(List<HealthPsyChatMemories> history, HealthChatStreamRequest request) {
        List<Message> messages = new ArrayList<>();
        String systemPrompt = request.getSystemPrompt();
        messages.add(new SystemMessage(systemPrompt == null || systemPrompt.isBlank() ? DEFAULT_SYSTEM_PROMPT : systemPrompt));

        for (HealthPsyChatMemories item : history) {
            if (item.getContent() == null || item.getContent().isBlank()) {
                continue;
            }
            String role = item.getRole();
            if ("assistant".equalsIgnoreCase(role) || "agent".equalsIgnoreCase(role)) {
                messages.add(new AssistantMessage(item.getContent()));
            } else if ("system".equalsIgnoreCase(role)) {
                messages.add(new SystemMessage(item.getContent()));
            } else {
                messages.add(new UserMessage(item.getContent()));
            }
        }

        messages.add(new UserMessage(request.getMessage()));
        return messages;
    }

    private void saveChatMemory(Long userId, String role, String content) {
        HealthPsyChatMemories memory = new HealthPsyChatMemories();
        memory.setUserId(userId);
        memory.setRole(role);
        memory.setContent(content);
        chatMemoriesService.save(memory);
    }

    private String extractContent(ChatResponse response) {
        if (response == null || response.getResult() == null || response.getResult().getOutput() == null) {
            return "";
        }
        String text = response.getResult().getOutput().getText();
        return text == null ? "" : text;
    }

    private String abbreviate(String value) {
        if (value == null) {
            return "null";
        }
        return value.length() <= 300 ? value : value.substring(0, 300) + "...(truncated)";
    }

    private ServerSentEvent<HealthChatStreamChunk> event(String type, String content, boolean done) {
        return ServerSentEvent.<HealthChatStreamChunk>builder()
                .event(type)
                .data(HealthChatStreamChunk.builder()
                        .type(type)
                        .content(content)
                        .done(done)
                        .timestamp(System.currentTimeMillis())
                        .build())
                .build();
    }
}
