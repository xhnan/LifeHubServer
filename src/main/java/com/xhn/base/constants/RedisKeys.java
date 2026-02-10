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

    /** 用户权限缓存 key 前缀：user:perms:{userId} */
    public static final String USER_PERMS_PREFIX = "user:perms:";

    public static String userPermsKey(Long userId) {
        return USER_PERMS_PREFIX + userId;
    }

    /** Token黑名单 key 前缀：token:blacklist:{token} */
    public static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";

    public static String tokenBlacklistKey(String token) {
        return TOKEN_BLACKLIST_PREFIX + token;
    }

}
