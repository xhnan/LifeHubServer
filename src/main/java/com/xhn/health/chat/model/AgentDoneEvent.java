package com.xhn.health.chat.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Agent done event data")
public class AgentDoneEvent {

    @Schema(description = "Completion status")
    private String status;
}
