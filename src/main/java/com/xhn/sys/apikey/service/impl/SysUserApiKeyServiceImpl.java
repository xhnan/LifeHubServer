package com.xhn.sys.apikey.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhn.sys.apikey.dto.SysUserApiKeyCreateRequest;
import com.xhn.sys.apikey.dto.SysUserApiKeyCreateResponse;
import com.xhn.sys.apikey.dto.SysUserApiKeyView;
import com.xhn.sys.apikey.mapper.SysUserApiKeyMapper;
import com.xhn.sys.apikey.model.SysUserApiKey;
import com.xhn.sys.apikey.service.SysUserApiKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysUserApiKeyServiceImpl extends ServiceImpl<SysUserApiKeyMapper, SysUserApiKey>
        implements SysUserApiKeyService {

    private static final String STATUS_ACTIVE = "active";
    private static final String STATUS_REVOKED = "revoked";
    private static final String STATUS_EXPIRED = "expired";
    private static final String KEY_PREFIX = "lhu_";
    private static final int RAW_KEY_SIZE = 32;
    private static final String DEFAULT_ALLOWED_PATH = "/sys/app-version/quick-publish";

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public SysUserApiKeyCreateResponse createApiKey(Long userId, SysUserApiKeyCreateRequest request) {
        if (userId == null) {
            throw new IllegalArgumentException("用户未登录");
        }
        if (request == null || !StringUtils.hasText(request.getKeyName())) {
            throw new IllegalArgumentException("keyName 不能为空");
        }
        if (request.getExpiresAt() != null && request.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("expiresAt 不能早于当前时间");
        }

        String rawApiKey = generateRawApiKey();
        String keyPrefix = rawApiKey.substring(0, Math.min(rawApiKey.length(), 12));
        LocalDateTime now = LocalDateTime.now();

        SysUserApiKey entity = new SysUserApiKey();
        entity.setUserId(userId);
        entity.setKeyName(request.getKeyName().trim());
        entity.setDescription(request.getDescription());
        entity.setKeyPrefix(keyPrefix);
        entity.setApiKeyHash(hashApiKey(rawApiKey));
        entity.setAllowedPaths(serializeAllowedPaths(request.getAllowedPaths()));
        entity.setStatus(STATUS_ACTIVE);
        entity.setExpiresAt(request.getExpiresAt());
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        this.save(entity);

        return new SysUserApiKeyCreateResponse(
                entity.getId(),
                rawApiKey,
                entity.getKeyName(),
                entity.getKeyPrefix(),
                deserializeAllowedPaths(entity.getAllowedPaths()),
                entity.getCreatedAt()
        );
    }

    @Override
    public List<SysUserApiKeyView> listByUserId(Long userId) {
        LambdaQueryWrapper<SysUserApiKey> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserApiKey::getUserId, userId)
                .orderByDesc(SysUserApiKey::getCreatedAt);

        return this.list(wrapper).stream()
                .map(item -> new SysUserApiKeyView(
                        item.getId(),
                        item.getKeyName(),
                        maskKey(item.getKeyPrefix()),
                        item.getDescription(),
                        deserializeAllowedPaths(item.getAllowedPaths()),
                        resolveStatus(item),
                        item.getLastUsedAt(),
                        item.getExpiresAt(),
                        item.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public boolean revokeById(Long userId, Long id) {
        LambdaQueryWrapper<SysUserApiKey> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserApiKey::getId, id)
                .eq(SysUserApiKey::getUserId, userId);

        SysUserApiKey existing = this.getOne(wrapper);
        if (existing == null) {
            return false;
        }

        SysUserApiKey update = new SysUserApiKey();
        update.setId(id);
        update.setStatus(STATUS_REVOKED);
        update.setUpdatedAt(LocalDateTime.now());
        return this.updateById(update);
    }

    @Override
    public Optional<SysUserApiKey> findValidApiKey(String rawApiKey) {
        if (!StringUtils.hasText(rawApiKey)) {
            return Optional.empty();
        }

        String hash = hashApiKey(rawApiKey);
        LambdaQueryWrapper<SysUserApiKey> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserApiKey::getApiKeyHash, hash)
                .eq(SysUserApiKey::getStatus, STATUS_ACTIVE)
                .last("LIMIT 1");

        SysUserApiKey apiKey = this.getOne(wrapper);
        if (apiKey == null) {
            return Optional.empty();
        }
        if (apiKey.getExpiresAt() != null && apiKey.getExpiresAt().isBefore(LocalDateTime.now())) {
            expireKey(apiKey);
            return Optional.empty();
        }
        return Optional.of(apiKey);
    }

    @Override
    public boolean isPathAllowed(SysUserApiKey apiKey, String requestPath) {
        if (apiKey == null || !StringUtils.hasText(requestPath)) {
            return false;
        }

        return deserializeAllowedPaths(apiKey.getAllowedPaths()).stream()
                .anyMatch(pattern -> matchesPath(pattern, requestPath));
    }

    @Override
    public void touchUsage(Long id) {
        if (id == null) {
            return;
        }
        SysUserApiKey update = new SysUserApiKey();
        update.setId(id);
        update.setLastUsedAt(LocalDateTime.now());
        update.setUpdatedAt(LocalDateTime.now());
        this.updateById(update);
    }

    private void expireKey(SysUserApiKey apiKey) {
        SysUserApiKey update = new SysUserApiKey();
        update.setId(apiKey.getId());
        update.setStatus(STATUS_EXPIRED);
        update.setUpdatedAt(LocalDateTime.now());
        this.updateById(update);
    }

    private String generateRawApiKey() {
        byte[] randomBytes = new byte[RAW_KEY_SIZE];
        secureRandom.nextBytes(randomBytes);
        return KEY_PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String hashApiKey(String rawApiKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawApiKey.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (Exception e) {
            log.error("Hash api key failed", e);
            throw new IllegalStateException("API Key 处理失败");
        }
    }

    private String maskKey(String prefix) {
        if (!StringUtils.hasText(prefix)) {
            return "****";
        }
        return prefix + "****";
    }

    private String serializeAllowedPaths(List<String> allowedPaths) {
        List<String> normalized = normalizeAllowedPaths(allowedPaths);
        return String.join(",", normalized);
    }

    private List<String> deserializeAllowedPaths(String allowedPaths) {
        if (!StringUtils.hasText(allowedPaths)) {
            return List.of(DEFAULT_ALLOWED_PATH);
        }
        return Arrays.stream(allowedPaths.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
    }

    private List<String> normalizeAllowedPaths(List<String> allowedPaths) {
        List<String> normalized = Optional.ofNullable(allowedPaths)
                .orElse(Collections.emptyList())
                .stream()
                .map(path -> path == null ? null : path.trim())
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());

        if (normalized.isEmpty()) {
            return List.of(DEFAULT_ALLOWED_PATH);
        }
        return normalized;
    }

    private boolean matchesPath(String pattern, String requestPath) {
        if (!StringUtils.hasText(pattern)) {
            return false;
        }
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return requestPath.equals(prefix) || requestPath.startsWith(prefix + "/");
        }
        return requestPath.equals(pattern);
    }

    private String resolveStatus(SysUserApiKey item) {
        if (item.getExpiresAt() != null && item.getExpiresAt().isBefore(LocalDateTime.now())
                && STATUS_ACTIVE.equals(item.getStatus())) {
            return STATUS_EXPIRED;
        }
        return item.getStatus();
    }
}
