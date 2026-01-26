package com.xhn.auth.service.impl;

import com.xhn.auth.model.LoginRequest;
import com.xhn.auth.model.LoginResponse;
import com.xhn.auth.service.AuthService;
import com.xhn.base.constants.RedisKeys;
import com.xhn.base.exception.ApplicationException;
import com.xhn.base.utils.JwtUtil;
import com.xhn.sys.role.model.SysRole;
import com.xhn.sys.user.model.SysUser;
import com.xhn.sys.user.service.SysUserService;
import com.xhn.sys.userrole.service.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证服务实现类
 * @author xhn
 * @date 2026-01-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final SysUserService sysUserService;
    private final PasswordEncoder passwordEncoder;
    private final SysUserRoleService sysUserRoleService;
    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // 1. 验证用户名和密码不能为空
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new ApplicationException("用户名和密码不能为空");
        }

        // 2. 根据用户名查询用户
        SysUser user = sysUserService.lambdaQuery()
                .eq(SysUser::getUsername, username)
                .one();
        
        if (user == null) {
            log.warn("登录失败：用户不存在，用户名: {}", username);
            throw new ApplicationException("用户名或密码错误");
        }

        // 3. 检查用户状态
        if ("inactive".equals(user.getStatus())) {
            log.warn("登录失败：用户账户未激活，用户名: {}", username);
            throw new ApplicationException("用户账户未激活");
        }
        
        if ("banned".equals(user.getStatus())) {
            log.warn("登录失败：用户账户已被封禁，用户名: {}", username);
            throw new ApplicationException("用户账户已被封禁");
        }

        // 4. 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            // 更新登录失败次数
            user.setLoginAttempts(user.getLoginAttempts() == null ? 1 : user.getLoginAttempts() + 1);
            sysUserService.updateById(user);
            
            log.warn("登录失败：密码错误，用户名: {}", username);
            throw new ApplicationException("用户名或密码错误");
        }

        // 5. 重置登录失败次数，更新最后登录时间
        user.setLoginAttempts(0);
        user.setLastLogin(LocalDateTime.now());
        sysUserService.updateById(user);

        // 6. 生成JWT token
        String token = jwtUtil.generateToken(user.getUserId());

        // token 有效期（毫秒），作为统一口径
        long tokenTtlMillis = jwtUtil.getExpirationMillis();
        Duration tokenTtl = jwtUtil.getExpirationDuration();

        // 7. 保存用户角色到Redis中（异步操作，不阻塞登录流程）
        List<SysRole> roles = sysUserRoleService.getRolesByUserId(user.getUserId());
        if (roles != null && !roles.isEmpty()) {
            List<String> roleCodes = roles.stream()
                    .map(SysRole::getRoleCode)
                    .collect(Collectors.toList());

            String redisKey = RedisKeys.userRolesKey(user.getUserId());
            reactiveRedisTemplate.opsForValue()
                    .set(redisKey, roleCodes, tokenTtl)
                    .doOnSuccess(v -> log.info("用户角色已保存到Redis，用户ID: {}, 角色列表: {}, ttl: {}ms", user.getUserId(), roleCodes, tokenTtlMillis))
                    .doOnError(e -> log.error("保存用户角色到Redis失败，用户ID: {}", user.getUserId(), e))
                    .onErrorResume(e -> {
                        // 即使Redis失败也不影响登录
                        log.warn("Redis操作失败，继续登录流程，用户ID: {}", user.getUserId());
                        return Mono.empty();
                    })
                    // 避免在当前线程里做IO（如果调用链在事件线程上，最好切走）
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe();
        } else {
            log.warn("用户未分配角色，用户ID: {}", user.getUserId());
        }

        // 8. 返回登录响应
        return new LoginResponse(token, tokenTtlMillis, username, "");
    }

    @Override
    public LoginResponse register(LoginRequest loginRequest) {
        // TODO: 实现用户注册逻辑
        // 1. 检查用户名是否已存在
        // 2. 密码加密
        // 3. 创建用户
        // 4. 生成token
        throw new UnsupportedOperationException("注册功能暂未实现");
    }

    @Override
    public LoginResponse refreshToken(String token) {
        // TODO: 实现token刷新逻辑
        // 1. 验证旧token
        // 2. 生成新token
        throw new UnsupportedOperationException("token刷新功能暂未实现");
    }

    @Override
    public void logout(String token) {
        // TODO: 实现登出逻辑
        // 可以将token加入黑名单
        log.info("用户登出，token: {}", token);
    }
}
