package com.xhn.base.filter;

import com.xhn.base.constants.RedisKeys;
import com.xhn.base.constants.SecurityConstants;
import com.xhn.sys.apikey.model.SysUserApiKey;
import com.xhn.sys.apikey.service.SysUserApiKeyService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ApiKeyFilter implements WebFilter {

    private static final String PROTECTED_PATH = "/sys/app-version/quick-publish";
    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String AUTH_TYPE_KEY = "authType";
    private static final String AUTH_TYPE_USER_API_KEY = "USER_API_KEY";
    private static final String AUTH_TYPE_LEGACY_API_KEY = "LEGACY_API_KEY";
    private static final String API_KEY_ID_KEY = "apiKeyId";
    private static final Long LEGACY_API_KEY_PRINCIPAL = -1L;

    private final SysUserApiKeyService sysUserApiKeyService;
    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    @Value("${app.api-key:}")
    private String configApiKey;

    @PostConstruct
    public void init() {
        log.info("========================================");
        log.info("ApiKeyFilter initialized");
        log.info("Protected path: {}", PROTECTED_PATH);
        log.info("Fallback API key configured: {}", StringUtils.hasText(configApiKey));
        log.info("========================================");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String path = request.getURI().getPath();
        String requestApiKey = extractApiKey(request);
        if (!StringUtils.hasText(requestApiKey)) {
            return chain.filter(exchange);
        }

        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .flatMap(existingAuth -> {
                    if (existingAuth != null && existingAuth.isAuthenticated()) {
                        return chain.filter(exchange);
                    }

                    return authenticateWithApiKeyLookup(chain, exchange, response, path, requestApiKey);
                })
                .switchIfEmpty(Mono.defer(() ->
                        authenticateWithApiKeyLookup(chain, exchange, response, path, requestApiKey)
                ));
    }

    private Mono<Void> authenticateWithApiKeyLookup(WebFilterChain chain,
                                                    ServerWebExchange exchange,
                                                    ServerHttpResponse response,
                                                    String path,
                                                    String requestApiKey) {
        return Mono.fromCallable(() -> sysUserApiKeyService.findValidApiKey(requestApiKey))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e -> new ApiKeyAuthenticationException("API Key validation failed", e))
                .flatMap(userApiKey -> authenticate(chain, exchange, response, path, requestApiKey, userApiKey))
                .onErrorResume(ApiKeyAuthenticationException.class, e -> {
                    log.error("API Key validation failed for path {}", path, e.getCause() != null ? e.getCause() : e);
                    if (response.isCommitted()) {
                        return Mono.error(e);
                    }
                    response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    return buildErrorResponse(response, 500, "API Key validation failed");
                });
    }

    private Mono<Void> authenticate(WebFilterChain chain,
                                    ServerWebExchange exchange,
                                    ServerHttpResponse response,
                                    String path,
                                    String requestApiKey,
                                    Optional<SysUserApiKey> userApiKey) {
        if (userApiKey.isPresent()) {
            if (!sysUserApiKeyService.isPathAllowed(userApiKey.get(), path)) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return buildErrorResponse(response, 403, "API Key is not allowed to access this path");
            }
            return authenticateUserApiKey(chain, exchange, userApiKey.get());
        }

        if (PROTECTED_PATH.equals(path) && StringUtils.hasText(configApiKey) && configApiKey.equals(requestApiKey)) {
            log.info("Fallback API Key validated successfully for {}", path);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(LEGACY_API_KEY_PRINCIPAL, null, Collections.emptyList());
            authentication.setDetails(Map.of(
                    AUTH_TYPE_KEY, AUTH_TYPE_LEGACY_API_KEY,
                    API_KEY_ID_KEY, 0L
            ));
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        }

        if (hasBearerToken(exchange.getRequest())) {
            return chain.filter(exchange);
        }

        response.setStatusCode(HttpStatus.FORBIDDEN);
        return buildErrorResponse(response, 403, "Invalid or expired API Key");
    }

    private Mono<Void> authenticateUserApiKey(WebFilterChain chain,
                                              ServerWebExchange exchange,
                                              SysUserApiKey apiKey) {
        return loadAuthorities(apiKey.getUserId())
                .flatMap(authorities -> {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(apiKey.getUserId(), null, authorities);
                    authentication.setDetails(Map.of(
                            AUTH_TYPE_KEY, AUTH_TYPE_USER_API_KEY,
                            API_KEY_ID_KEY, apiKey.getId()
                    ));

                    log.info("User API Key validated, userId={}, keyId={}", apiKey.getUserId(), apiKey.getId());

                    Mono<Void> touchUsageMono = Mono.fromRunnable(() -> sysUserApiKeyService.touchUsage(apiKey.getId()))
                            .subscribeOn(Schedulers.boundedElastic())
                            .then()
                            .onErrorResume(e -> {
                                log.warn("Failed to update API Key last_used_at, keyId={}", apiKey.getId(), e);
                                return Mono.empty();
                            });

                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
                            .then(touchUsageMono);
                });
    }

    private boolean hasBearerToken(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst("Authorization");
        return StringUtils.hasText(authorization) && authorization.startsWith("Bearer ");
    }

    private Mono<List<GrantedAuthority>> loadAuthorities(Long userId) {
        String authoritiesKey = RedisKeys.userPermsKey(userId);
        return reactiveRedisTemplate.opsForValue()
                .get(authoritiesKey)
                .defaultIfEmpty(Collections.emptyList())
                .map(this::convertAuthorities);
    }

    private List<GrantedAuthority> convertAuthorities(Object value) {
        @SuppressWarnings("unchecked")
        List<String> authoritiesStr = value instanceof List ? (List<String>) value : Collections.emptyList();

        if (authoritiesStr.contains(SecurityConstants.ALL_PERMISSIONS_AUTHORITY)) {
            authoritiesStr = List.of(
                    SecurityConstants.ALL_PERMISSIONS_AUTHORITY,
                    SecurityConstants.SUPER_ADMIN_AUTHORITY
            );
        }

        return authoritiesStr.stream()
                .filter(a -> a != null && !a.isBlank())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private String extractApiKey(ServerHttpRequest request) {
        String requestApiKey = request.getHeaders().getFirst(API_KEY_HEADER);
        if (!StringUtils.hasText(requestApiKey)) {
            requestApiKey = request.getHeaders().getFirst("api-key");
        }
        if (!StringUtils.hasText(requestApiKey)) {
            requestApiKey = request.getHeaders().getFirst("x-api-key");
        }
        return requestApiKey;
    }

    private Mono<Void> buildErrorResponse(ServerHttpResponse response, int code, String message) {
        if (response.isCommitted()) {
            return Mono.empty();
        }
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String responseBody = String.format("{\"code\":%d,\"message\":\"%s\"}", code, message);
        DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    private static final class ApiKeyAuthenticationException extends RuntimeException {
        private ApiKeyAuthenticationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
