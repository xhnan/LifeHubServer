package com.xhn.base.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Spring Security 工具类。
 */
public class SecurityUtils {

    public static final String AUTH_TYPE_KEY = "authType";
    public static final String API_KEY_ID_KEY = "apiKeyId";

    public static Mono<Long> getCurrentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> {
                    Authentication auth = ctx.getAuthentication();
                    if (auth != null && auth.getPrincipal() instanceof Long userId) {
                        return userId;
                    }
                    return null;
                })
                .flatMap(userId -> userId == null ? Mono.empty() : Mono.just(userId));
    }

    public static Mono<Authentication> getAuthentication() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication());
    }

    public static Mono<Long> getCurrentApiKeyId() {
        return getAuthentication()
                .map(SecurityUtils::readApiKeyId)
                .flatMap(apiKeyId -> apiKeyId == null ? Mono.empty() : Mono.just(apiKeyId));
    }

    public static Mono<String> getCurrentAuthType() {
        return getAuthentication()
                .map(SecurityUtils::readAuthType)
                .flatMap(authType -> authType == null ? Mono.empty() : Mono.just(authType));
    }

    private static Long readApiKeyId(Authentication authentication) {
        if (authentication == null || !(authentication.getDetails() instanceof Map<?, ?> details)) {
            return null;
        }
        Object value = details.get(API_KEY_ID_KEY);
        if (value instanceof Number number) {
            return number.longValue();
        }
        return null;
    }

    private static String readAuthType(Authentication authentication) {
        if (authentication == null || !(authentication.getDetails() instanceof Map<?, ?> details)) {
            return null;
        }
        Object value = details.get(AUTH_TYPE_KEY);
        return value instanceof String str ? str : null;
    }
}
