package com.xhn.base.constants;

/**
 * Redis key 统一管理
 */
public final class RedisKeys {

    private RedisKeys() {
    }

    /** 用户角色缓存 key 前缀：user:roles:{userId} */
    public static final String USER_ROLES_PREFIX = "user:roles:";

    public static String userRolesKey(Long userId) {
        return USER_ROLES_PREFIX + userId;
    }
}
