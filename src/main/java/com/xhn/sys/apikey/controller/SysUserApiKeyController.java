package com.xhn.sys.apikey.controller;

import com.xhn.base.utils.SecurityUtils;
import com.xhn.response.ResponseResult;
import com.xhn.sys.apikey.dto.SysUserApiKeyCreateRequest;
import com.xhn.sys.apikey.dto.SysUserApiKeyCreateResponse;
import com.xhn.sys.apikey.dto.SysUserApiKeyView;
import com.xhn.sys.apikey.service.SysUserApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sys/user-api-keys")
@Tag(name = "用户 API Key", description = "用户创建并管理第三方调用用的 API Key")
public class SysUserApiKeyController {

    private final SysUserApiKeyService sysUserApiKeyService;

    @PostMapping
    @Operation(summary = "创建 API Key", description = "创建一个属于当前登录用户的 API Key，明文只返回一次")
    public Mono<ResponseResult<SysUserApiKeyCreateResponse>> create(@RequestBody SysUserApiKeyCreateRequest request) {
        return SecurityUtils.getCurrentUserId()
                .switchIfEmpty(Mono.error(new IllegalStateException("未获取到当前用户")))
                .flatMap(userId -> Mono.fromCallable(() -> sysUserApiKeyService.createApiKey(userId, request))
                        .subscribeOn(Schedulers.boundedElastic()))
                .map(data -> ResponseResult.success("API Key 创建成功，请妥善保存，明文仅返回一次；未传 allowedPaths 时默认仅开放 quick-publish", data))
                .onErrorResume(e -> Mono.just(ResponseResult.error(e.getMessage())));
    }

    @GetMapping
    @Operation(summary = "查询我的 API Key 列表")
    public Mono<ResponseResult<List<SysUserApiKeyView>>> list() {
        return SecurityUtils.getCurrentUserId()
                .switchIfEmpty(Mono.error(new IllegalStateException("未获取到当前用户")))
                .flatMap(userId -> Mono.fromCallable(() -> sysUserApiKeyService.listByUserId(userId))
                        .subscribeOn(Schedulers.boundedElastic()))
                .map(ResponseResult::success)
                .onErrorResume(e -> Mono.just(ResponseResult.error(e.getMessage())));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "撤销 API Key", description = "撤销后第三方将无法继续使用该 API Key")
    public Mono<ResponseResult<Boolean>> revoke(
            @Parameter(description = "API Key ID") @PathVariable Long id) {
        return SecurityUtils.getCurrentUserId()
                .switchIfEmpty(Mono.error(new IllegalStateException("未获取到当前用户")))
                .flatMap(userId -> Mono.fromCallable(() -> sysUserApiKeyService.revokeById(userId, id))
                        .subscribeOn(Schedulers.boundedElastic()))
                .map(result -> result
                        ? ResponseResult.success(true)
                        : ResponseResult.<Boolean>error("API Key 不存在或无权限操作"))
                .onErrorResume(e -> Mono.just(ResponseResult.error(e.getMessage())));
    }
}
