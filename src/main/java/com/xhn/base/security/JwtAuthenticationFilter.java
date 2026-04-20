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
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        String bearerToken = token.substring(7);
        if (!jwtUtil.validateToken(bearerToken)) {
            return chain.filter(exchange);
        }

        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .flatMap(existingAuth -> {
                    if (existingAuth != null && existingAuth.isAuthenticated()) {
                        return chain.filter(exchange);
                    }

                    Long userId = jwtUtil.getUserIdFromToken(bearerToken);
                    return loadAuthorities(userId)
                            .flatMap(authorities -> {
                                UsernamePasswordAuthenticationToken authentication =
                                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
                                authentication.setDetails(Map.of("authType", "JWT"));

                                return chain.filter(exchange)
                                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                            });
                })
                .switchIfEmpty(Mono.defer(() -> {
                    Long userId = jwtUtil.getUserIdFromToken(bearerToken);
                    return loadAuthorities(userId)
                            .flatMap(authorities -> {
                                UsernamePasswordAuthenticationToken authentication =
                                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
                                authentication.setDetails(Map.of("authType", "JWT"));

                                return chain.filter(exchange)
                                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                            });
                }))
                .onErrorResume(e -> chain.filter(exchange));
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
}
