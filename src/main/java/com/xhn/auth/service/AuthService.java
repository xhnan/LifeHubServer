package com.xhn.auth.service;

import com.xhn.auth.model.LoginRequest;
import com.xhn.auth.model.LoginResponse;

/**
 * 认证服务接口
 * @author xhn
 * @date 2026-01-24
 */
public interface AuthService {

    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录响应，包含JWT token
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 用户注册
     * @param loginRequest 注册请求（包含用户名和密码）
     * @return 注册响应，包含JWT token
     */
    LoginResponse register(LoginRequest loginRequest);

    /**
     * 刷新token
     * @param token 当前token
     * @return 新的token响应
     */
    LoginResponse refreshToken(String token);

    /**
     * 登出
     * @param token 要失效的token
     * @return 操作结果
     */
    void logout(String token);
}