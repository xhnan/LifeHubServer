package com.xhn.health.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xhn.base.utils.SecurityUtils;
import com.xhn.health.chat.model.HealthAgentResponse;
import com.xhn.health.chat.model.HealthChatStreamChunk;
import com.xhn.health.chat.model.HealthChatStreamRequest;
import com.xhn.health.chat.model.PythonAgentRequest;
import com.xhn.health.chat.service.HealthChatService;
import com.xhn.health.chat.service.PythonAgentService;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/health/chat", "/api/health/chat"})
@Tag(name = "health-chat", description = "Health chat")
public class HealthChatController {

    private final HealthChatService healthChatService;
    private final PythonAgentService pythonAgentService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Send message and receive stream response")
    public Flux<ServerSentEvent<HealthChatStreamChunk>> streamChat(@RequestBody @Valid HealthChatStreamRequest request) {
        return SecurityUtils.getCurrentUserId()
                .flatMapMany(userId -> healthChatService.streamChat(userId, request));
    }

    @PostMapping("/agent")
    @Operation(summary = "Call health agent and get final result")
    public Mono<ResponseResult<HealthAgentResponse>> callAgent(@RequestBody @Valid HealthChatStreamRequest request) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    PythonAgentRequest agentRequest = PythonAgentRequest.builder()
                            .message(request.getMessage())
                            .user_id(request.getUserIdForAgent() != null ? request.getUserIdForAgent() : String.valueOf(userId))
                            .build();

                    return pythonAgentService.callAgentStream(agentRequest)
                            .filter(sse -> "final".equals(sse.event()) || "error".equals(sse.event()))
                            .next()
                            .flatMap(sse -> {
                                if ("error".equals(sse.event())) {
                                    return Mono.error(new RuntimeException("Agent call failed: " + sse.data()));
                                }
                                try {
                                    HealthAgentResponse response = objectMapper.readValue(sse.data(), HealthAgentResponse.class);
                                    return Mono.just(ResponseResult.success(response));
                                } catch (Exception e) {
                                    return Mono.error(new RuntimeException("Failed to parse agent response", e));
                                }
                            });
                })
                .onErrorResume(error -> Mono.just(ResponseResult.error("Agent call failed: " + error.getMessage())));
    }
}
