package com.xhn.base.constants;

/**
 * @author xhn
 * @date 2026/1/26 16:58
 * @description
 */
public class SecurityConstants {
    /**
     * 超级管理员角色编码
     */
    public static final String SUPER_ADMIN_ROLE_CODE = "SUPER_ADMIN";

    /**
     * 普通管理员角色编码
     */
    public static final String ADMIN_ROLE_CODE = "ADMIN";


    /**
     * 白名单路径
     */
    public static final String[] WHITE_LIST = {
            "/auth/**",
            "/public/**",
            "/ws/**",  // WebSocket路径
            "/swagger-ui/**",  // Swagger UI
            "/swagger-ui.html",  // Swagger UI HTML
            "/webjars/**",  // Swagger webjars
            "/v3/api-docs/**",  // OpenAPI 3.0 docs
            "/swagger-resources/**",  // Swagger resources
            "/actuator/**",  // Actuator endpoints (可选)
            "/wechat/callback/**"  // 企业微信回调接口
    };

    /** Spring Security 角色前缀 */
    public static final String ROLE_PREFIX = "ROLE_";

    /** 超级管理员在 Spring Security 中对应的 authority：ROLE_SUPER_ADMIN */
    public static final String SUPER_ADMIN_AUTHORITY = ROLE_PREFIX + SUPER_ADMIN_ROLE_CODE;

    /**
     * 超级管理员“全权限”占位符 authority。
     * 约定：当用户拥有该 authority 时，任何 hasAuthority(...) 校验都认为通过。
     */
    public static final String ALL_PERMISSIONS_AUTHORITY = "*:*:*";
}
