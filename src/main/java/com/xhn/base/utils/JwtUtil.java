package com.xhn.base.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JWT工具类
 * 用于生成、解析和验证JWT令牌
 *
 * @author xhn
 * @date 2026-01-24
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}")
    private long expiration; // 默认24小时,可配置

    /**
     * 生成密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成Token(仅包含用户ID)
     *
     * @param userId 用户ID
     * @return JWT token
     */
    public String generateToken(Long userId) {
        return generateToken(userId, new HashMap<>());
    }

    /**
     * 生成Token(包含用户ID和权限信息)
     *
     * @param userId 用户ID
     * @param permissions 权限列表(可为null)
     * @return JWT token
     */
    public String generateToken(Long userId, List<String> permissions) {
        Map<String, Object> claims = new HashMap<>();
        if (permissions != null && !permissions.isEmpty()) {
            claims.put("permissions", permissions);
        }
        return generateToken(userId, claims);
    }

    /**
     * 生成Token(包含自定义声明)
     *
     * @param userId 用户ID
     * @param claims 自定义声明
     * @return JWT token
     */
    public String generateToken(Long userId, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析Token
     *
     * @param token JWT token
     * @return Claims对象
     * @throws JwtException token无效或过期
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.error("Token解析失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token JWT token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        return Long.valueOf(parseToken(token).getSubject());
    }

    /**
     * 从Token中获取权限列表
     *
     * @param token JWT token
     * @return 权限列表
     */
    @SuppressWarnings("unchecked")
    public List<String> getPermissionsFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("permissions", List.class);
    }

    /**
     * 从Token中获取自定义声明
     *
     * @param token JWT token
     * @param claimKey 声明键
     * @param clazz 返回类型
     * @return 声明值
     */
    public <T> T getClaimFromToken(String token, String claimKey, Class<T> clazz) {
        Claims claims = parseToken(token);
        return claims.get(claimKey, clazz);
    }

    /**
     * 验证Token是否有效
     *
     * @param token JWT token
     * @return true-有效,false-无效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("Token已过期");
            return false;
        } catch (JwtException e) {
            log.debug("Token无效: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Token验证异常", e);
            return false;
        }
    }

    /**
     * 检查Token是否即将过期
     *
     * @param token JWT token
     * @param threshold 阈值(毫秒),默认30分钟
     * @return true-即将过期,false-未过期
     */
    public boolean isTokenExpiringSoon(String token, long threshold) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            long timeUntilExpiry = expiration.getTime() - System.currentTimeMillis();
            return timeUntilExpiry < threshold;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 刷新Token(保留原有的claims)
     *
     * @param token 旧的JWT token
     * @return 新的JWT token
     */
    public String refreshToken(String token) {
        try {
            Claims claims = parseToken(token);
            Long userId = Long.valueOf(claims.getSubject());

            // 移除标准的时间相关声明,使用新的时间
            claims.remove(Claims.ISSUED_AT);
            claims.remove(Claims.EXPIRATION);

            return generateToken(userId, new HashMap<>(claims));
        } catch (Exception e) {
            log.error("刷新Token失败", e);
            throw new JwtException("无法刷新Token", e);
        }
    }

    /**
     * 获取Token过期时间
     *
     * @param token JWT token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return parseToken(token).getExpiration();
    }

    /**
     * 检查Token是否过期
     *
     * @param token JWT token
     * @return true-已过期,false-未过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            // 包括ExpiredJwtException和其他异常都视为已过期
            return true;
        }
    }
}