package com.xhn.health.chat.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Agent error event data")
public class AgentErrorEvent {

    @Schema(description = "Error message")
    private String message;

    @Schema(description = "Detailed error information")
    private String detail;
}
