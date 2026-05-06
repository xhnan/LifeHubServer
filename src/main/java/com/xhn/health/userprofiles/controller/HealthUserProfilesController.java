package com.xhn.health.userprofiles.controller;

import com.xhn.base.utils.SecurityUtils;
import com.xhn.health.userprofiles.model.HealthUserProfiles;
import com.xhn.health.userprofiles.service.HealthUserProfilesService;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 用户健康档案控制器
 *
 * @author xhn
 * @date 2026-03-13
 */
@RestController
@RequestMapping("/health/user-profiles")
@Tag(name = "health", description = "健康管理")
public class HealthUserProfilesController {

    @Autowired
    private HealthUserProfilesService healthUserProfilesService;

    @PostMapping
    @Operation(summary = "新增用户健康档案")
    public Mono<ResponseResult<Boolean>> add(
            @RequestBody HealthUserProfiles healthUserProfile
    ) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    healthUserProfile.setUserId(userId);
                    boolean result = healthUserProfilesService.save(healthUserProfile);
                    return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除用户健康档案")
    public Mono<ResponseResult<Boolean>> delete(
            @Parameter(description = "档案ID") @PathVariable Long id
    ) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .flatMap(userId -> {
                    HealthUserProfiles profile = healthUserProfilesService.getById(id);
                    if (profile == null) {
                        return Mono.just(ResponseResult.<Boolean>error("档案不存在"));
                    }
                    if (!userId.equals(profile.getUserId())) {
                        return Mono.just(ResponseResult.<Boolean>error("只能删除自己的档案"));
                    }
                    boolean result = healthUserProfilesService.removeById(id);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.<Boolean>error("删除失败"));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @PutMapping
    @Operation(summary = "修改用户健康档案")
    public ResponseResult<Boolean> update(
            @RequestBody HealthUserProfiles healthUserProfile
    ) {
        boolean result = healthUserProfilesService.updateById(healthUserProfile);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询用户健康档案")
    public ResponseResult<HealthUserProfiles> getById(
            @Parameter(description = "档案ID") @PathVariable Long id
    ) {
        HealthUserProfiles healthUserProfile = healthUserProfilesService.getById(id);
        return healthUserProfile != null ? ResponseResult.success(healthUserProfile) : ResponseResult.error("查询失败");
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的健康档案")
    public Mono<ResponseResult<HealthUserProfiles>> getMyProfile() {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    HealthUserProfiles profile = healthUserProfilesService.getUserProfileByUserId(userId);
                    return profile != null ? ResponseResult.success(profile) : ResponseResult.<HealthUserProfiles>error("未找到健康档案");
                });
    }

    @PostMapping("/init")
    @Operation(summary = "初始化或更新我的健康档案")
    public Mono<ResponseResult<Boolean>> initOrUpdateProfile(
            @RequestBody HealthUserProfiles healthUserProfile
    ) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    healthUserProfile.setUserId(userId);
                    HealthUserProfiles existingProfile = healthUserProfilesService.getUserProfileByUserId(userId);
                    boolean result;
                    if (existingProfile != null) {
                        healthUserProfile.setId(existingProfile.getId());
                        result = healthUserProfilesService.updateById(healthUserProfile);
                    } else {
                        result = healthUserProfilesService.save(healthUserProfile);
                    }
                    return result ? ResponseResult.success(true) : ResponseResult.error("操作失败");
                });
    }
}
