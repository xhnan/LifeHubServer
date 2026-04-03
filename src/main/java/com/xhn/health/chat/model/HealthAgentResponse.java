package com.xhn.health.chat.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Health Agent sync response")
public class HealthAgentResponse {

    @Schema(description = "Complete agent execution state")
    private Map<String, Object> state;

    @Schema(description = "Quick summary for business logic")
    private Map<String, Object> summary;

    @Schema(description = "Structured payload for database recording")
    private Map<String, Object> record_payload;
}
