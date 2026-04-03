package com.xhn.health.chat.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Python Agent API request")
public class PythonAgentRequest {

    @Schema(description = "User message", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    @Schema(description = "User ID (optional, defaults to 'local-user' if not provided)")
    private String user_id;
}
