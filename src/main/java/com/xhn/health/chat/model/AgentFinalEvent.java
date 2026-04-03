package com.xhn.health.chat.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "Agent final event data")
public class AgentFinalEvent {

    @Schema(description = "Complete agent state")
    private Map<String, Object> state;

    @Schema(description = "Quick summary for business logic")
    private Map<String, Object> summary;

    @Schema(description = "Structured payload for database recording")
    private Map<String, Object> record_payload;
}
