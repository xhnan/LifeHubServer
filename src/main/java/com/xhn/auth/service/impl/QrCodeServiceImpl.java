package com.xhn.auth.service.impl;

import com.xhn.auth.model.QrCodeLoginInfo;
import com.xhn.auth.service.QrCodeService;
import com.xhn.base.constants.RedisKeys;
import com.xhn.base.constants.SecurityConstants;
import com.xhn.base.exception.ApplicationException;
import com.xhn.base.utils.JwtUtil;
import com.xhn.sys.role.model.SysRole;
import com.xhn.sys.user.model.SysUser;
import com.xhn.sys.user.service.SysUserService;
import com.xhn.sys.userrole.service.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 二维码登录服务实现
 *
 * @author xhn
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QrCodeServiceImpl implements QrCodeService {

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private final JwtUtil jwtUtil;
    private final SysUserService sysUserService;
    private final SysUserRoleService sysUserRoleService;

    /** 二维码有效期（秒） */
    private static final long QRCODE_TTL_SECONDS = 300;

    @Override
    public Mono<String> generateQrCode() {
        String qrCodeId = UUID.randomUUID().toString().replace("-", "");
        QrCodeLoginInfo info = new QrCodeLoginInfo();
        info.setStatus(QrCodeLoginInfo.PENDING);

        String key = RedisKeys.qrcodeKey(qrCodeId);
        return reactiveRedisTemplate.opsForValue()
                .set(key, info, Duration.ofSeconds(QRCODE_TTL_SECONDS))
                .doOnSuccess(v -> log.info("生成二维码，qrCodeId: {}", qrCodeId))
                .thenReturn(qrCodeId);
    }

    @Override
    public Mono<QrCodeLoginInfo> getQrCodeStatus(String qrCodeId) {
        String key = RedisKeys.qrcodeKey(qrCodeId);
        return reactiveRedisTemplate.opsForValue().get(key)
                .map(obj -> (QrCodeLoginInfo) obj)
                .switchIfEmpty(Mono.fromCallable(() -> {
                    QrCodeLoginInfo expired = new QrCodeLoginInfo();
                    expired.setStatus(QrCodeLoginInfo.EXPIRED);
                    return expired;
                }));
    }

    @Override
    public Mono<Void> scanQrCode(String qrCodeId, Long userId) {
        return getQrCodeInfo(qrCodeId)
                .flatMap(info -> {
                    if (!QrCodeLoginInfo.PENDING.equals(info.getStatus())) {
                        return Mono.error(new ApplicationException("二维码状态无效，无法扫码"));
                    }
                    info.setStatus(QrCodeLoginInfo.SCANNED);
                    info.setUserId(userId);
                    return saveQrCodeInfo(qrCodeId, info).then(Mono.empty());
                })
                .doOnSuccess(v -> log.info("二维码已扫码，qrCodeId: {}, userId: {}", qrCodeId, userId))
                .then();
    }

    @Override
    public Mono<QrCodeLoginInfo> confirmQrCode(String qrCodeId, Long userId) {
        return getQrCodeInfo(qrCodeId)
                .flatMap(info -> {
                    if (!QrCodeLoginInfo.SCANNED.equals(info.getStatus())) {
                        return Mono.error(new ApplicationException("二维码状态无效，无法确认"));
                    }
                    if (!userId.equals(info.getUserId())) {
                        return Mono.error(new ApplicationException("操作用户与扫码用户不一致"));
                    }

                    // 查询用户信息（阻塞JDBC → 弹性线程池）
                    return Mono.fromCallable(() -> sysUserService.getById(userId))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(user -> {
                                if (user == null) {
                                    return Mono.error(new ApplicationException("用户不存在"));
                                }
                                if ("banned".equals(user.getStatus())) {
                                    return Mono.error(new ApplicationException("用户已被封禁"));
                                }

                                // 生成 JWT token
                                String token = jwtUtil.generateToken(userId);
                                String refreshToken = jwtUtil.generateToken(userId, new HashMap<>(), jwtUtil.getRefreshExpirationMillis());
                                long tokenTtlMillis = jwtUtil.getExpirationMillis();

                                // 异步缓存角色和权限（fire-and-forget）
                                cacheUserAuthorities(userId, jwtUtil.getExpirationDuration());

                                // 更新二维码信息
                                info.setStatus(QrCodeLoginInfo.CONFIRMED);
                                info.setToken(token);
                                info.setRefreshToken(refreshToken);
                                info.setExpiresIn(tokenTtlMillis);
                                info.setUsername(user.getUsername());
                                info.setAvatar(user.getAvatarUrl());

                                return saveQrCodeInfo(qrCodeId, info).thenReturn(info);
                            });
                })
                .doOnNext(i -> log.info("二维码登录已确认，qrCodeId: {}, userId: {}, username: {}", qrCodeId, userId, i.getUsername()));
    }

    @Override
    public Mono<Void> cancelQrCode(String qrCodeId, Long userId) {
        return getQrCodeInfo(qrCodeId)
                .flatMap(info -> {
                    if (!QrCodeLoginInfo.SCANNED.equals(info.getStatus())) {
                        return Mono.error(new ApplicationException("二维码状态无效，无法取消"));
                    }
                    if (!userId.equals(info.getUserId())) {
                        return Mono.error(new ApplicationException("操作用户与扫码用户不一致"));
                    }
                    info.setStatus(QrCodeLoginInfo.CANCELLED);
                    return saveQrCodeInfo(qrCodeId, info).then(Mono.empty());
                })
                .doOnSuccess(v -> log.info("二维码登录已取消，qrCodeId: {}, userId: {}", qrCodeId, userId))
                .then();
    }

    // ==================== 私有方法 ====================

    private Mono<QrCodeLoginInfo> getQrCodeInfo(String qrCodeId) {
        String key = RedisKeys.qrcodeKey(qrCodeId);
        return reactiveRedisTemplate.opsForValue().get(key)
                .map(obj -> (QrCodeLoginInfo) obj)
                .switchIfEmpty(Mono.error(new ApplicationException("二维码已过期或不存在")));
    }

    private Mono<Boolean> saveQrCodeInfo(String qrCodeId, QrCodeLoginInfo info) {
        String key = RedisKeys.qrcodeKey(qrCodeId);
        return reactiveRedisTemplate.getExpire(key)
                .flatMap(ttl -> {
                    if (ttl != null && !ttl.isZero() && !ttl.isNegative()) {
                        return reactiveRedisTemplate.opsForValue().set(key, info, ttl);
                    }
                    return Mono.error(new ApplicationException("二维码已过期"));
                });
    }

    /**
     * 异步缓存用户角色和权限到 Redis（复用 AuthServiceImpl 的缓存逻辑）
     */
    private void cacheUserAuthorities(Long userId, Duration tokenTtl) {
        Mono.fromCallable(() -> sysUserRoleService.getRolesByUserId(userId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(roles -> {
                    if (roles == null || roles.isEmpty()) {
                        log.warn("二维码登录用户未分配角色，userId: {}", userId);
                        return Mono.empty();
                    }

                    List<String> roleCodes = roles.stream()
                            .map(SysRole::getRoleCode)
                            .collect(Collectors.toList());

                    List<String> authorities = roleCodes.stream()
                            .filter(rc -> rc != null && !rc.isBlank())
                            .map(rc -> SecurityConstants.ROLE_PREFIX + rc)
                            .collect(Collectors.toList());

                    if (roleCodes.contains(SecurityConstants.SUPER_ADMIN_ROLE_CODE)) {
                        authorities.add(SecurityConstants.ALL_PERMISSIONS_AUTHORITY);
                    }

                    String rolesKey = RedisKeys.userRolesKey(userId);
                    String permsKey = RedisKeys.userPermsKey(userId);

                    return reactiveRedisTemplate.opsForValue().set(rolesKey, roleCodes, tokenTtl)
                            .then(reactiveRedisTemplate.opsForValue().set(permsKey, authorities, tokenTtl))
                            .doOnSuccess(v -> log.info("二维码登录用户权限已缓存，userId: {}, 角色: {}", userId, roleCodes))
                            .onErrorResume(e -> {
                                log.warn("Redis缓存权限失败，userId: {}", userId, e);
                                return Mono.empty();
                            });
                })
                .subscribe();
    }
}
