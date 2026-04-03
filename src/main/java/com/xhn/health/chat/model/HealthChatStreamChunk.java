package com.xhn.health.chat.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Health chat stream chunk")
public class HealthChatStreamChunk {

    @Schema(description = "Event type: start, delta, complete, error")
    private String type;

    @Schema(description = "Chunk content")
    private String content;

    @Schema(description = "Whether the stream is done")
    private Boolean done;

    @Schema(description = "Server timestamp")
    private Long timestamp;
}
