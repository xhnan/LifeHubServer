package com.xhn.health.chat.service;

import com.xhn.health.chat.model.PythonAgentRequest;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;

/**
 * Service for calling Python Health Agent API
 */
public interface PythonAgentService {

    /**
     * Call Python Agent stream API and return SSE events
     *
     * @param request Agent request
     * @return Flux of SSE events containing string data
     */
    Flux<ServerSentEvent<String>> callAgentStream(PythonAgentRequest request);
}
