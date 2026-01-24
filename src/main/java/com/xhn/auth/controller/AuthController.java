package com.xhn.auth.controller;


import com.xhn.auth.model.LoginRequest;
import com.xhn.auth.model.LoginResponse;
import com.xhn.base.utils.JwtUtil;
import com.xhn.response.ResponseResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * @author xhn
 * @date 2026-01-24
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求参数（会自动验证）
     * @return 返回 token
     */
    @PostMapping("/login")
    public ResponseResult<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // TODO: 实现登录逻辑
        // 1. 验证用户名密码（从数据库查询用户信息）
        // 2. 验证密码是否正确（BCrypt 比对）
        // 3. 生成 JWT token

        // 示例：生成 token
        String token = jwtUtil.generateToken(loginRequest.getUsername());
        LoginResponse response = new LoginResponse(token, 86400000L, loginRequest.getUsername());

        return ResponseResult.success(response);
    }

}
