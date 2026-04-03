package com.xhn.health.chat.service;

import com.xhn.health.chat.model.HealthChatStreamChunk;
import com.xhn.health.chat.model.HealthChatStreamRequest;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface HealthChatService {

    Flux<ServerSentEvent<HealthChatStreamChunk>> streamChat(Long userId, HealthChatStreamRequest request);
}
