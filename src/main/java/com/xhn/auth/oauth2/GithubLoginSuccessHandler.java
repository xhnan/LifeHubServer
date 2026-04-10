package com.xhn.auth.oauth2;

import com.xhn.base.constants.RedisKeys;
import com.xhn.base.constants.SecurityConstants;
import com.xhn.base.utils.JwtUtil;
import com.xhn.sys.role.model.SysRole;
import com.xhn.sys.role.service.SysRoleService;
import com.xhn.sys.user.model.SysUser;
import com.xhn.sys.user.service.SysUserService;
import com.xhn.sys.userrole.model.SysUserRole;
import com.xhn.sys.userrole.service.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * GitHub OAuth2 登录成功处理器（响应式）
 * <p>
 * 处理流程：
 * 1. 从 OAuth2User 提取 GitHub 用户信息
 * 2. 根据 githubId 查找已有用户，若不存在则尝试通过 email 关联，仍不存在则自动注册
 * 3. 生成 JWT access/refresh token
 * 4. 缓存用户角色和权限到 Redis
 * 5. 重定向到前端页面并携带 token 参数
 *
 * @author xhn
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GithubLoginSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final SysUserService sysUserService;
    private final SysUserRoleService sysUserRoleService;
    private final SysRoleService sysRoleService;
    private final JwtUtil jwtUtil;
    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Value("${oauth2.frontend-redirect-url:http://localhost:5173/#/oauth2/redirect}")
    private String frontendRedirectUrl;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        // 1. 提取 GitHub 用户信息
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String githubId = oauth2User.getAttribute("id").toString();
        String loginName = oauth2User.getAttribute("login");
        String avatarUrl = oauth2User.getAttribute("avatar_url");
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        log.info("GitHub OAuth2 登录回调，githubId: {}, login: {}", githubId, loginName);

        // 2. 查找或创建用户（阻塞数据库操作，切换到弹性线程池）
        return Mono.fromCallable(() -> findOrCreateUser(githubId, loginName, avatarUrl, email, name))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(user -> {
                    // 3. 检查用户状态
                    if ("banned".equals(user.getStatus())) {
                        log.warn("GitHub 登录用户已被封禁，userId: {}", user.getUserId());
                        return redirectWithError(webFilterExchange, "account_banned");
                    }

                    // 4. 生成 JWT token
                    String accessToken = jwtUtil.generateToken(user.getUserId());
                    String refreshToken = jwtUtil.generateToken(user.getUserId(), new HashMap<>(), jwtUtil.getRefreshExpirationMillis());
                    long tokenTtlMillis = jwtUtil.getExpirationMillis();
                    Duration tokenTtl = jwtUtil.getExpirationDuration();

                    // 5. 异步缓存角色和权限到 Redis
                    cacheUserAuthorities(user.getUserId(), tokenTtl);

                    // 6. 重定向到前端
                    log.info("GitHub 登录成功，userId: {}, username: {}", user.getUserId(), user.getUsername());
                    return redirectWithToken(webFilterExchange, accessToken, refreshToken, tokenTtlMillis, user.getUsername(), user.getAvatarUrl());
                })
                .onErrorResume(e -> {
                    log.error("GitHub 登录处理失败", e);
                    return redirectWithError(webFilterExchange, "auth_failed");
                });
    }

    /**
     * 查找已有用户或自动注册新用户
     * <p>
     * 查找顺序：
     * 1. 根据 githubId 查找（已有 GitHub 关联）
     * 2. 根据 email 查找（关联已有账号）
     * 3. 自动注册新用户
     */
    private SysUser findOrCreateUser(String githubId, String loginName, String avatarUrl, String email, String name) {
        // 1. 根据 githubId 查找
        SysUser user = sysUserService.lambdaQuery()
                .eq(SysUser::getGithubId, githubId)
                .one();

        if (user != null) {
            user.setLastLogin(LocalDateTime.now());
            if (avatarUrl != null) {
                user.setAvatarUrl(avatarUrl);
            }
            sysUserService.updateById(user);
            log.info("GitHub 用户登录，userId: {}, githubId: {}", user.getUserId(), githubId);
            return user;
        }

        // 2. 根据 email 关联已有账号
        if (email != null && !email.isEmpty()) {
            user = sysUserService.lambdaQuery()
                    .eq(SysUser::getEmail, email)
                    .one();
            if (user != null) {
                user.setGithubId(githubId);
                if (avatarUrl != null) {
                    user.setAvatarUrl(avatarUrl);
                }
                user.setLastLogin(LocalDateTime.now());
                sysUserService.updateById(user);
                log.info("关联 GitHub 账号到已有用户，userId: {}, email: {}", user.getUserId(), email);
                return user;
            }
        }

        // 3. 自动注册新用户
        user = new SysUser();
        user.setGithubId(githubId);
        user.setUsername(generateUniqueUsername(loginName));
        user.setFullName(name != null && !name.isEmpty() ? name : loginName);
        user.setAvatarUrl(avatarUrl);
        user.setEmail(email);
        user.setStatus("active");
        user.setLoginAttempts(0);
        user.setLastLogin(LocalDateTime.now());
        user.setEmailVerified(email != null && !email.isEmpty());
        // OAuth2 用户不需要密码，设置随机密码防止直接登录
        user.setPassword(PASSWORD_ENCODER.encode(UUID.randomUUID().toString()));
        sysUserService.save(user);

        // 分配默认角色
        assignDefaultRole(user.getUserId());

        log.info("GitHub 用户自动注册成功，userId: {}, username: {}, githubId: {}", user.getUserId(), user.getUsername(), githubId);
        return user;
    }

    /**
     * 生成唯一用户名，避免冲突
     */
    private String generateUniqueUsername(String loginName) {
        String baseName = "github_" + loginName;
        if (sysUserService.lambdaQuery().eq(SysUser::getUsername, baseName).one() == null) {
            return baseName;
        }
        return baseName + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 为新注册用户分配默认角色（USER）
     */
    private void assignDefaultRole(Long userId) {
        SysRole defaultRole = sysRoleService.lambdaQuery()
                .eq(SysRole::getRoleCode, SecurityConstants.DEFAULT_USER_ROLE_CODE)
                .one();
        if (defaultRole == null) {
            log.warn("默认角色 {} 不存在，跳过角色分配，userId: {}", SecurityConstants.DEFAULT_USER_ROLE_CODE, userId);
            return;
        }
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(defaultRole.getId());
        sysUserRoleService.save(userRole);
        log.info("已为用户分配默认角色，userId: {}, roleCode: {}", userId, SecurityConstants.DEFAULT_USER_ROLE_CODE);
    }

    /**
     * 异步缓存用户角色和权限到 Redis（fire-and-forget，不阻塞登录流程）
     */
    private void cacheUserAuthorities(Long userId, Duration tokenTtl) {
        Mono.fromCallable(() -> sysUserRoleService.getRolesByUserId(userId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(roles -> {
                    if (roles == null || roles.isEmpty()) {
                        log.warn("GitHub 用户未分配角色，userId: {}", userId);
                        return Mono.empty();
                    }

                    List<String> roleCodes = roles.stream()
                            .map(SysRole::getRoleCode)
                            .collect(Collectors.toList());

                    List<String> authorities = roleCodes.stream()
                            .filter(rc -> rc != null && !rc.isBlank())
                            .map(rc -> SecurityConstants.ROLE_PREFIX + rc)
                            .collect(Collectors.toList());

                    if (roleCodes.contains(SecurityConstants.SUPER_ADMIN_ROLE_CODE)) {
                        authorities.add(SecurityConstants.ALL_PERMISSIONS_AUTHORITY);
                    }

                    String rolesKey = RedisKeys.userRolesKey(userId);
                    String permsKey = RedisKeys.userPermsKey(userId);

                    return reactiveRedisTemplate.opsForValue().set(rolesKey, roleCodes, tokenTtl)
                            .then(reactiveRedisTemplate.opsForValue().set(permsKey, authorities, tokenTtl))
                            .doOnSuccess(v -> log.info("GitHub 用户权限已缓存，userId: {}, 角色: {}", userId, roleCodes))
                            .onErrorResume(e -> {
                                log.warn("Redis 缓存权限失败，继续登录流程，userId: {}", userId, e);
                                return Mono.empty();
                            });
                })
                .subscribe();
    }

    /**
     * 携带 token 重定向到前端
     */
    private Mono<Void> redirectWithToken(WebFilterExchange webFilterExchange, String accessToken, String refreshToken,
                                          long expiresIn, String username, String avatarUrl) {
        String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
        String encodedAvatar = avatarUrl != null ? URLEncoder.encode(avatarUrl, StandardCharsets.UTF_8) : "";
        String redirectUrl = String.format("%s?token=%s&refreshToken=%s&expiresIn=%d&username=%s&avatar=%s",
                frontendRedirectUrl, accessToken, refreshToken, expiresIn, encodedUsername, encodedAvatar);
        return redirect(webFilterExchange, redirectUrl);
    }

    /**
     * 携带错误信息重定向到前端
     */
    private Mono<Void> redirectWithError(WebFilterExchange webFilterExchange, String error) {
        return redirect(webFilterExchange, frontendRedirectUrl + "?error=" + error);
    }

    private Mono<Void> redirect(WebFilterExchange webFilterExchange, String url) {
        var response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(URI.create(url));
        return response.setComplete();
    }
}
