package com.xhn.base.security;

import com.xhn.base.constants.SecurityConstants;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

/**
 * 允许超级管理员全放行：如果拥有 ALL_PERMISSIONS_AUTHORITY，则直接授权。
 */
public class AllPermissionReactiveAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final ReactiveAuthorizationManager<AuthorizationContext> delegate;

    public AllPermissionReactiveAuthorizationManager(ReactiveAuthorizationManager<AuthorizationContext> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext context) {
        return authentication
                .filter(Authentication::isAuthenticated)
                .flatMap(auth -> auth.getAuthorities().stream()
                        .anyMatch(a -> SecurityConstants.ALL_PERMISSIONS_AUTHORITY.equals(a.getAuthority()))
                        ? Mono.just(new AuthorizationDecision(true))
                        : delegate.check(Mono.just(auth), context));
    }
}
