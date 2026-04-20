package com.xhn.sys.apikey.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.sys.apikey.dto.SysUserApiKeyCreateRequest;
import com.xhn.sys.apikey.dto.SysUserApiKeyCreateResponse;
import com.xhn.sys.apikey.dto.SysUserApiKeyView;
import com.xhn.sys.apikey.model.SysUserApiKey;

import java.util.List;
import java.util.Optional;

public interface SysUserApiKeyService extends IService<SysUserApiKey> {

    SysUserApiKeyCreateResponse createApiKey(Long userId, SysUserApiKeyCreateRequest request);

    List<SysUserApiKeyView> listByUserId(Long userId);

    boolean revokeById(Long userId, Long id);

    Optional<SysUserApiKey> findValidApiKey(String rawApiKey);

    boolean isPathAllowed(SysUserApiKey apiKey, String requestPath);

    void touchUsage(Long id);
}
