package com.xhn.base.utils;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

/**
 * Spring Security 工具类
 * 用于获取当前登录用户信息
 *
 * @author xhn
 * @date 2026-01-26
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户ID
     * @return 用户ID
     */
    public static Mono<Long> getCurrentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> {
                    Authentication auth = ctx.getAuthentication();
                    if (auth != null && auth.getPrincipal() instanceof Long) {
                        return (Long) auth.getPrincipal();
                    }
                    return null;
                });
    }

    /**
     * 获取当前Authentication对象
     * @return Authentication
     */
    public static Mono<Authentication> getAuthentication() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication());
    }
}
