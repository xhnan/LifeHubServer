package com.xhn.health.chat.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xhn.health.chat.model.PythonAgentRequest;
import com.xhn.health.chat.service.PythonAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementation of Python Agent service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PythonAgentServiceImpl implements PythonAgentService {

    private static final ParameterizedTypeReference<ServerSentEvent<String>> SSE_STRING_TYPE =
            new ParameterizedTypeReference<>() {};

    private final WebClient.Builder webClientBuilder;
    @SuppressWarnings("unused")
    private final ObjectMapper objectMapper;

    @Value("${health.agent.api.base-url:http://localhost:8000}")
    private String agentApiBaseUrl;

    @Override
    public Flux<ServerSentEvent<String>> callAgentStream(PythonAgentRequest request) {
        WebClient webClient = webClientBuilder
                .baseUrl(agentApiBaseUrl)
                .build();

        return webClient.post()
                .uri("/api/health/agent/stream")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> {
                            log.error("Python Agent API error: {}", clientResponse.statusCode());
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(
                                            new RuntimeException("Agent API error: " + errorBody)
                                    ));
                        }
                )
                .bodyToFlux(SSE_STRING_TYPE)
                .filter(sse -> sse.data() != null || sse.event() != null)
                .doOnSubscribe(subscription -> log.info("Python Agent stream request sent to {}", agentApiBaseUrl))
                .doOnNext(sse -> log.info("Python Agent event received: event={}, data={}", sse.event(), abbreviate(sse.data())))
                .doOnError(error -> log.error("Error calling Python Agent API", error))
                .doOnComplete(() -> log.info("Python Agent stream completed"));
    }

    private String abbreviate(String value) {
        if (value == null) {
            return "null";
        }
        return value.length() <= 300 ? value : value.substring(0, 300) + "...(truncated)";
    }
}
