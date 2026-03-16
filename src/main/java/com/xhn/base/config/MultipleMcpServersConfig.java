package com.xhn.base.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xhn.health.mcp.UserProfilesMcpService;
import com.xhn.health.mcp.WeightLogsMcpService;
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

    @Bean(name = "userProfilesTransport")
    public WebFluxSseServerTransportProvider userProfilesTransport(ObjectMapper objectMapper) {
        return WebFluxSseServerTransportProvider.builder()
                .jsonMapper(new JacksonMcpJsonMapper(objectMapper))
                .basePath("")
                .sseEndpoint("/health/mcp/user-profiles/sse")
                .messageEndpoint("/health/mcp/user-profiles/message")
                .build();
    }

    @Bean(name = "userProfilesTools")
    public List<McpServerFeatures.AsyncToolSpecification> userProfilesTools(UserProfilesMcpService userProfilesMcpService) {
        var provider = MethodToolCallbackProvider.builder()
                .toolObjects(userProfilesMcpService)
                .build();
        return McpToolUtils.toAsyncToolSpecifications(provider.getToolCallbacks());
    }

    @Bean
    public McpAsyncServer userProfilesMcpServer(
            @Qualifier("userProfilesTransport") WebFluxSseServerTransportProvider userProfilesTransport,
            @Qualifier("userProfilesTools") List<McpServerFeatures.AsyncToolSpecification> userProfilesTools) {

        return McpServer.async(userProfilesTransport)
                .serverInfo("User Profiles Service", "1.0.0")
                .tools(userProfilesTools)
                .build();
    }

    @Bean
    @Order(2)
    public RouterFunction<ServerResponse> userProfilesRouter(
            @Qualifier("userProfilesTransport") WebFluxSseServerTransportProvider userProfilesTransport) {
        return (RouterFunction<ServerResponse>) userProfilesTransport.getRouterFunction();
    }

    @Bean(name = "weightLogsTransport")
    public WebFluxSseServerTransportProvider weightLogsTransport(ObjectMapper objectMapper) {
        return WebFluxSseServerTransportProvider.builder()
                .jsonMapper(new JacksonMcpJsonMapper(objectMapper))
                .basePath("")
                .sseEndpoint("/health/mcp/weight-logs/sse")
                .messageEndpoint("/health/mcp/weight-logs/message")
                .build();
    }

    @Bean(name = "weightLogsTools")
    public List<McpServerFeatures.AsyncToolSpecification> weightLogsTools(WeightLogsMcpService weightLogsMcpService) {
        var provider = MethodToolCallbackProvider.builder()
                .toolObjects(weightLogsMcpService)
                .build();
        return McpToolUtils.toAsyncToolSpecifications(provider.getToolCallbacks());
    }

    @Bean
    public McpAsyncServer weightLogsMcpServer(
            @Qualifier("weightLogsTransport") WebFluxSseServerTransportProvider weightLogsTransport,
            @Qualifier("weightLogsTools") List<McpServerFeatures.AsyncToolSpecification> weightLogsTools) {

        return McpServer.async(weightLogsTransport)
                .serverInfo("Weight Logs Service", "1.0.0")
                .tools(weightLogsTools)
                .build();
    }

    @Bean
    @Order(3)
    public RouterFunction<ServerResponse> weightLogsRouter(
            @Qualifier("weightLogsTransport") WebFluxSseServerTransportProvider weightLogsTransport) {
        return (RouterFunction<ServerResponse>) weightLogsTransport.getRouterFunction();
    }
}
