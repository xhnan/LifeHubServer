package com.xhn.base.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xhn.health.mcp.ActivityMcpService;
import com.xhn.health.mcp.DietMcpService;
import com.xhn.health.mcp.HealthManagerMcpService;
import com.xhn.health.mcp.MentalSupportMcpService;
import com.xhn.health.mcp.RiskGuardMcpService;
import com.xhn.health.mcp.WeightTrendMcpService;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.transport.WebFluxSseServerTransportProvider;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.List;

@Configuration
public class MultipleMcpServersConfig {

    @Bean(name = "healthManagerTransport")
    public WebFluxSseServerTransportProvider healthManagerTransport(ObjectMapper objectMapper) {
        return transport(objectMapper, "/health/mcp/health-manager/sse", "/health/mcp/health-manager/message");
    }

    @Bean(name = "healthManagerTools")
    public List<McpServerFeatures.AsyncToolSpecification> healthManagerTools(HealthManagerMcpService service) {
        return toTools(service);
    }

    @Bean
    public McpAsyncServer healthManagerMcpServer(
            @Qualifier("healthManagerTransport") WebFluxSseServerTransportProvider transport,
            @Qualifier("healthManagerTools") List<McpServerFeatures.AsyncToolSpecification> tools) {
        return server("Health Manager MCP", transport, tools);
    }

    @Bean
    @Order(2)
    public RouterFunction<ServerResponse> healthManagerRouter(
            @Qualifier("healthManagerTransport") WebFluxSseServerTransportProvider transport) {
        return (RouterFunction<ServerResponse>) transport.getRouterFunction();
    }

    @Bean(name = "dietTransport")
    public WebFluxSseServerTransportProvider dietTransport(ObjectMapper objectMapper) {
        return transport(objectMapper, "/health/mcp/diet/sse", "/health/mcp/diet/message");
    }

    @Bean(name = "dietTools")
    public List<McpServerFeatures.AsyncToolSpecification> dietTools(DietMcpService service) {
        return toTools(service);
    }

    @Bean
    public McpAsyncServer dietMcpServer(
            @Qualifier("dietTransport") WebFluxSseServerTransportProvider transport,
            @Qualifier("dietTools") List<McpServerFeatures.AsyncToolSpecification> tools) {
        return server("Diet MCP", transport, tools);
    }

    @Bean
    @Order(3)
    public RouterFunction<ServerResponse> dietRouter(
            @Qualifier("dietTransport") WebFluxSseServerTransportProvider transport) {
        return (RouterFunction<ServerResponse>) transport.getRouterFunction();
    }

    @Bean(name = "weightTrendTransport")
    public WebFluxSseServerTransportProvider weightTrendTransport(ObjectMapper objectMapper) {
        return transport(objectMapper, "/health/mcp/weight-trend/sse", "/health/mcp/weight-trend/message");
    }

    @Bean(name = "weightTrendTools")
    public List<McpServerFeatures.AsyncToolSpecification> weightTrendTools(WeightTrendMcpService service) {
        return toTools(service);
    }

    @Bean
    public McpAsyncServer weightTrendMcpServer(
            @Qualifier("weightTrendTransport") WebFluxSseServerTransportProvider transport,
            @Qualifier("weightTrendTools") List<McpServerFeatures.AsyncToolSpecification> tools) {
        return server("Weight Trend MCP", transport, tools);
    }

    @Bean
    @Order(4)
    public RouterFunction<ServerResponse> weightTrendRouter(
            @Qualifier("weightTrendTransport") WebFluxSseServerTransportProvider transport) {
        return (RouterFunction<ServerResponse>) transport.getRouterFunction();
    }

    @Bean(name = "activityTransport")
    public WebFluxSseServerTransportProvider activityTransport(ObjectMapper objectMapper) {
        return transport(objectMapper, "/health/mcp/activity/sse", "/health/mcp/activity/message");
    }

    @Bean(name = "activityTools")
    public List<McpServerFeatures.AsyncToolSpecification> activityTools(ActivityMcpService service) {
        return toTools(service);
    }

    @Bean
    public McpAsyncServer activityMcpServer(
            @Qualifier("activityTransport") WebFluxSseServerTransportProvider transport,
            @Qualifier("activityTools") List<McpServerFeatures.AsyncToolSpecification> tools) {
        return server("Activity MCP", transport, tools);
    }

    @Bean
    @Order(5)
    public RouterFunction<ServerResponse> activityRouter(
            @Qualifier("activityTransport") WebFluxSseServerTransportProvider transport) {
        return (RouterFunction<ServerResponse>) transport.getRouterFunction();
    }

    @Bean(name = "mentalSupportTransport")
    public WebFluxSseServerTransportProvider mentalSupportTransport(ObjectMapper objectMapper) {
        return transport(objectMapper, "/health/mcp/mental-support/sse", "/health/mcp/mental-support/message");
    }

    @Bean(name = "mentalSupportTools")
    public List<McpServerFeatures.AsyncToolSpecification> mentalSupportTools(MentalSupportMcpService service) {
        return toTools(service);
    }

    @Bean
    public McpAsyncServer mentalSupportMcpServer(
            @Qualifier("mentalSupportTransport") WebFluxSseServerTransportProvider transport,
            @Qualifier("mentalSupportTools") List<McpServerFeatures.AsyncToolSpecification> tools) {
        return server("Mental Support MCP", transport, tools);
    }

    @Bean
    @Order(6)
    public RouterFunction<ServerResponse> mentalSupportRouter(
            @Qualifier("mentalSupportTransport") WebFluxSseServerTransportProvider transport) {
        return (RouterFunction<ServerResponse>) transport.getRouterFunction();
    }

    @Bean(name = "riskGuardTransport")
    public WebFluxSseServerTransportProvider riskGuardTransport(ObjectMapper objectMapper) {
        return transport(objectMapper, "/health/mcp/risk-guard/sse", "/health/mcp/risk-guard/message");
    }

    @Bean(name = "riskGuardTools")
    public List<McpServerFeatures.AsyncToolSpecification> riskGuardTools(RiskGuardMcpService service) {
        return toTools(service);
    }

    @Bean
    public McpAsyncServer riskGuardMcpServer(
            @Qualifier("riskGuardTransport") WebFluxSseServerTransportProvider transport,
            @Qualifier("riskGuardTools") List<McpServerFeatures.AsyncToolSpecification> tools) {
        return server("Risk Guard MCP", transport, tools);
    }

    @Bean
    @Order(7)
    public RouterFunction<ServerResponse> riskGuardRouter(
            @Qualifier("riskGuardTransport") WebFluxSseServerTransportProvider transport) {
        return (RouterFunction<ServerResponse>) transport.getRouterFunction();
    }

    private WebFluxSseServerTransportProvider transport(ObjectMapper objectMapper, String sseEndpoint, String messageEndpoint) {
        return WebFluxSseServerTransportProvider.builder()
                .jsonMapper(new JacksonMcpJsonMapper(objectMapper))
                .basePath("")
                .sseEndpoint(sseEndpoint)
                .messageEndpoint(messageEndpoint)
                .build();
    }

    private List<McpServerFeatures.AsyncToolSpecification> toTools(Object toolObject) {
        var provider = MethodToolCallbackProvider.builder()
                .toolObjects(toolObject)
                .build();
        return McpToolUtils.toAsyncToolSpecifications(provider.getToolCallbacks());
    }

    private McpAsyncServer server(
            String name,
            WebFluxSseServerTransportProvider transport,
            List<McpServerFeatures.AsyncToolSpecification> tools) {
        return McpServer.async(transport)
                .serverInfo(name, "1.0.0")
                .tools(tools)
                .build();
    }
}
