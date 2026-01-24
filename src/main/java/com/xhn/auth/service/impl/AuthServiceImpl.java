package com.xhn.auth.service.impl;

import com.xhn.auth.model.LoginRequest;
import com.xhn.auth.model.LoginResponse;
import com.xhn.auth.service.AuthService;
import com.xhn.base.utils.JwtUtil;
import com.xhn.sys.user.model.SysUser;
import com.xhn.sys.user.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

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

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // 1. 验证用户名和密码不能为空
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new IllegalArgumentException("用户名和密码不能为空");
        }

        // 2. 根据用户名查询用户
        SysUser user = sysUserService.lambdaQuery()
                .eq(SysUser::getUsername, username)
                .one();
        
        if (user == null) {
            log.warn("登录失败：用户不存在，用户名: {}", username);
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 3. 检查用户状态
        if ("inactive".equals(user.getStatus())) {
            log.warn("登录失败：用户账户未激活，用户名: {}", username);
            throw new IllegalArgumentException("用户账户未激活");
        }
        
        if ("banned".equals(user.getStatus())) {
            log.warn("登录失败：用户账户已被封禁，用户名: {}", username);
            throw new IllegalArgumentException("用户账户已被封禁");
        }

        // 4. 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            // 更新登录失败次数
            user.setLoginAttempts(user.getLoginAttempts() == null ? 1 : user.getLoginAttempts() + 1);
            sysUserService.updateById(user);
            
            log.warn("登录失败：密码错误，用户名: {}", username);
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 5. 重置登录失败次数，更新最后登录时间
        user.setLoginAttempts(0);
        user.setLastLogin(LocalDateTime.now());
        sysUserService.updateById(user);

        // 6. 生成JWT token
        String token = jwtUtil.generateToken(username);
        
        // 7. 返回登录响应
        return new LoginResponse(token, 86400000L, username);
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