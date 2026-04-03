package com.xhn.health.chat.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Health chat stream request")
public class HealthChatStreamRequest {

    @NotBlank(message = "message must not be blank")
    @Schema(description = "User message", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    @Min(value = 0, message = "historyLimit must be >= 0")
    @Max(value = 20, message = "historyLimit must be <= 20")
    @Schema(description = "History message count", defaultValue = "10")
    private Integer historyLimit = 10;

    @Schema(description = "Optional system prompt")
    private String systemPrompt;

    @Schema(description = "Use Python Agent (default: true)")
    private Boolean useAgent = true;

    @Schema(description = "User ID for Python Agent (optional)")
    private String userIdForAgent;
}
