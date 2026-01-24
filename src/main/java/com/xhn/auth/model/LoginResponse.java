package com.xhn.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应
 * @author xhn
 * @date 2026-01-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * JWT token
     */
    private String token;

    /**
     * token 类型
     */
    private String tokenType = "Bearer";

    /**
     * 过期时间（毫秒）
     */
    private Long expiresIn;

    /**
     * 用户名
     */
    private String username;


    //头像
    private String avatar;

    public LoginResponse(String token, Long expiresIn, String username, String avatar) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.username = username;
    }
}
