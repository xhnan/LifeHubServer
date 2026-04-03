package com.xhn.base.filter;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * API-Key 验证过滤器
 * 用于保护敏感接口，验证请求头中的 API-Key
 * <p>
 * 适用于 Spring WebFlux 响应式环境
 *
 * @author xhn
 * @date 2026-03-02
 */
@Slf4j
@Component
public class ApiKeyFilter implements WebFilter {

    @Value("${app.api-key:}")
    private String configApiKey;

    private static final String API_KEY_HEADER = "X-API-Key";

    /**
     * 初始化时打印日志，确认 Bean 被加载
     */
    @PostConstruct
    public void init() {
        log.info("========================================");
        log.info("ApiKeyFilter initialized!");
        log.info("Configured API-Key: {}",
            configApiKey != null && !configApiKey.isEmpty() ? "***" + configApiKey.substring(Math.max(0, configApiKey.length() - 3)) : "NOT SET");
        log.info("Will protect path: /sys/app-version/quick-publish");
        log.info("========================================");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String path = request.getURI().getPath();
        String method = request.getMethod().name();

        log.debug("ApiKeyFilter invoked - Method: {}, Path: {}", method, path);

        // 只拦截 POST /sys/app-version/quick-publish
        if (!path.equals("/sys/app-version/quick-publish")) {
            return chain.filter(exchange);
        }

        log.info("API-Key filter triggered for path: {}", path);

        // 从请求头获取 API-Key
        String requestApiKey = request.getHeaders().getFirst(API_KEY_HEADER);
        if (requestApiKey == null) {
            requestApiKey = request.getHeaders().getFirst("api-key");
        }
        if (requestApiKey == null) {
            requestApiKey = request.getHeaders().getFirst("x-api-key");
        }

        // 验证 API-Key
        if (configApiKey == null || configApiKey.isEmpty()) {
            log.error("API-Key not configured in application properties");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return buildErrorResponse(response, 500, "API-Key未配置");
        }

        if (requestApiKey == null || requestApiKey.isEmpty()) {
            log.warn("API-Key missing from request: {}", path);
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return buildErrorResponse(response, 401, "缺少API-Key，请在请求头中添加 X-API-Key");
        }

        if (!configApiKey.equals(requestApiKey)) {
            log.warn("Invalid API-Key for request: {}, received: {}, expected: {}",
                path, requestApiKey, configApiKey);
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return buildErrorResponse(response, 403, "无效的API-Key");
        }

        log.info("API-Key validated successfully for: {}", path);
        return chain.filter(exchange);
    }

    /**
     * 构建错误响应
     */
    private Mono<Void> buildErrorResponse(ServerHttpResponse response, int code, String message) {
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String responseBody = String.format("{\"code\":%d,\"message\":\"%s\"}", code, message);
        DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }
}
