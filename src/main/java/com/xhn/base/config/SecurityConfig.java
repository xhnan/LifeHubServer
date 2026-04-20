package com.xhn.base.config;

import com.xhn.auth.oauth2.GithubLoginSuccessHandler;
import com.xhn.base.constants.SecurityConstants;
import com.xhn.base.filter.ApiKeyFilter;
import com.xhn.base.security.AllPermissionReactiveAuthorizationManager;
import com.xhn.base.security.JsonAuthenticationEntryPoint;
import com.xhn.base.security.JwtAuthenticationFilter;
import com.xhn.base.utils.JwtUtil;
import com.xhn.sys.apikey.service.SysUserApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private GithubLoginSuccessHandler successHandler;

    @Autowired
    private JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint;

    @Bean
    public ApiKeyFilter apiKeyFilter(SysUserApiKeyService sysUserApiKeyService,
                                     ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        return new ApiKeyFilter(sysUserApiKeyService, reactiveRedisTemplate);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil,
                                                           ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        return new JwtAuthenticationFilter(jwtUtil, reactiveRedisTemplate);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ApiKeyFilter apiKeyFilter,
                                                         JwtAuthenticationFilter jwtAuthenticationFilter) {
        ReactiveAuthorizationManager<AuthorizationContext> authenticatedOnly =
                (Mono<Authentication> authentication, AuthorizationContext context) ->
                        authentication
                                .map(Authentication::isAuthenticated)
                                .defaultIfEmpty(false)
                                .map(AuthorizationDecision::new);

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(SecurityConstants.WHITE_LIST).permitAll()
                        .anyExchange().access(new AllPermissionReactiveAuthorizationManager(authenticatedOnly))
                )
                .oauth2Login(oAuth2LoginSpec -> oAuth2LoginSpec
                        .authenticationSuccessHandler(successHandler)
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jsonAuthenticationEntryPoint)
                )
                .addFilterAt(apiKeyFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
