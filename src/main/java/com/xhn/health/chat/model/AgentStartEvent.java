package com.xhn.health.chat.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Agent start event data")
public class AgentStartEvent {

    @Schema(description = "User ID")
    private String user_id;

    @Schema(description = "User message")
    private String message;
}
