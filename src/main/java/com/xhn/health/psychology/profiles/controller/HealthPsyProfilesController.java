package com.xhn.health.psychology.profiles.controller;

import com.xhn.base.utils.SecurityUtils;
import com.xhn.health.psychology.profiles.model.HealthPsyProfiles;
import com.xhn.health.psychology.profiles.service.HealthPsyProfilesService;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * 心理档案控制器
 *
 * @author xhn
 * @date 2026-03-16
 */
@RestController
@RequestMapping("/health/psychology/profiles")
@Tag(name = "health-psychology", description = "心理健康管理")
public class HealthPsyProfilesController {

    @Autowired
    private HealthPsyProfilesService healthPsyProfilesService;

    @PostMapping
    @Operation(summary = "新增心理档案")
    public Mono<ResponseResult<Boolean>> add(
            @RequestBody HealthPsyProfiles healthPsyProfiles
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    healthPsyProfiles.setUserId(userId);
                    boolean result = healthPsyProfilesService.save(healthPsyProfiles);
                    return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除心理档案")
    public Mono<ResponseResult<Boolean>> delete(
            @Parameter(description = "档案ID") @PathVariable Long id
    ) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    HealthPsyProfiles profile = healthPsyProfilesService.getById(id);
                    if (profile == null) {
                        return Mono.just(ResponseResult.<Boolean>error("档案不存在"));
                    }
                    if (!userId.equals(profile.getUserId())) {
                        return Mono.just(ResponseResult.<Boolean>error("只能删除自己的档案"));
                    }
                    boolean result = healthPsyProfilesService.removeById(id);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.<Boolean>error("删除失败"));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @PutMapping
    @Operation(summary = "修改心理档案")
    public ResponseResult<Boolean> update(
            @RequestBody HealthPsyProfiles healthPsyProfiles
    ) {
        boolean result = healthPsyProfilesService.updateById(healthPsyProfiles);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询心理档案")
    public ResponseResult<HealthPsyProfiles> getById(
            @Parameter(description = "档案ID") @PathVariable Long id
    ) {
        HealthPsyProfiles healthPsyProfiles = healthPsyProfilesService.getById(id);
        return healthPsyProfiles != null ? ResponseResult.success(healthPsyProfiles) : ResponseResult.error("查询失败");
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的心理档案")
    public Mono<ResponseResult<HealthPsyProfiles>> getMyProfile() {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    HealthPsyProfiles profile = healthPsyProfilesService.getProfileByUserId(userId);
                    return profile != null ? ResponseResult.success(profile) : ResponseResult.<HealthPsyProfiles>error("未找到心理档案");
                });
    }

    @PostMapping("/init")
    @Operation(summary = "初始化或更新我的心理档案")
    public Mono<ResponseResult<Boolean>> initOrUpdateProfile(
            @RequestBody HealthPsyProfiles healthPsyProfiles
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    healthPsyProfiles.setUserId(userId);
                    HealthPsyProfiles existingProfile = healthPsyProfilesService.getProfileByUserId(userId);
                    boolean result;
                    if (existingProfile != null) {
                        healthPsyProfiles.setId(existingProfile.getId());
                        result = healthPsyProfilesService.updateById(healthPsyProfiles);
                    } else {
                        result = healthPsyProfilesService.save(healthPsyProfiles);
                    }
                    return result ? ResponseResult.success(true) : ResponseResult.error("操作失败");
                });
    }
}
