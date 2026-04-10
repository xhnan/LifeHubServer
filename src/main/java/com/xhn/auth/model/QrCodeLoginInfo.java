package com.xhn.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 二维码登录信息，存储在 Redis 中
 *
 * @author xhn
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeLoginInfo {

    public static final String PENDING = "PENDING";
    public static final String SCANNED = "SCANNED";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String CANCELLED = "CANCELLED";
    public static final String EXPIRED = "EXPIRED";

    /** 二维码状态 */
    private String status;

    /** 扫码用户ID */
    private Long userId;

    /** JWT accessToken（确认后填充） */
    private String token;

    /** 刷新 token（确认后填充） */
    private String refreshToken;

    /** token 有效期（毫秒） */
    private Long expiresIn;

    /** 用户名 */
    private String username;

    /** 用户头像 */
    private String avatar;
}
