package com.xhn.base.security;

import com.xhn.base.constants.RedisKeys;
import com.xhn.base.constants.SecurityConstants;
import com.xhn.base.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (token == null || !token.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        token = token.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return chain.filter(exchange);
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        String authoritiesKey = RedisKeys.userPermsKey(userId);

        return reactiveRedisTemplate.opsForValue()
                .get(authoritiesKey)
                .defaultIfEmpty(Collections.emptyList())
                .flatMap(val -> {
                    @SuppressWarnings("unchecked")
                    List<String> authoritiesStr = (val instanceof List) ? (List<String>) val : Collections.emptyList();

                    // 如果是超级管理员，全权限占位符让后续鉴权统一放行
                    if (authoritiesStr.contains(SecurityConstants.ALL_PERMISSIONS_AUTHORITY)) {
                        authoritiesStr = List.of(SecurityConstants.ALL_PERMISSIONS_AUTHORITY, SecurityConstants.SUPER_ADMIN_AUTHORITY);
                    }

                    List<GrantedAuthority> authorities = authoritiesStr.stream()
                            .filter(a -> a != null && !a.isBlank())
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, authorities);

                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                })
                // Redis 取不到/反序列化失败时，也不要把请求直接打挂
                .onErrorResume(e -> chain.filter(exchange));
    }
}