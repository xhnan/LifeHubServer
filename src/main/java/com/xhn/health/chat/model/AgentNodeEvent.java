package com.xhn.health.chat.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "Agent node event data")
public class AgentNodeEvent {

    @Schema(description = "Node name (e.g., intent_agent, diet_agent)")
    private String node;

    @Schema(description = "Incremental update from this node")
    private Map<String, Object> patch;
}
