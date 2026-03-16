package com.xhn.health.userprofiles.controller;

import com.xhn.base.utils.SecurityUtils;
import com.xhn.health.userprofiles.model.HealthyUserProfiles;
import com.xhn.health.userprofiles.service.HealthyUserProfilesService;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * 用户健康档案控制器
 *
 * @author xhn
 * @date 2026-03-13
 */
@RestController
@RequestMapping("/health/user-profiles")
@Tag(name = "health", description = "健康管理")
public class HealthyUserProfilesController {

    @Autowired
    private HealthyUserProfilesService healthyUserProfilesService;

    @PostMapping
    @Operation(summary = "新增用户健康档案")
    public Mono<ResponseResult<Boolean>> add(
            @RequestBody HealthyUserProfiles healthyUserProfiles
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    healthyUserProfiles.setUserId(userId);
                    boolean result = healthyUserProfilesService.save(healthyUserProfiles);
                    return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除用户健康档案")
    public Mono<ResponseResult<Boolean>> delete(
            @Parameter(description = "档案ID") @PathVariable Long id
    ) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    HealthyUserProfiles profile = healthyUserProfilesService.getById(id);
                    if (profile == null) {
                        return Mono.just(ResponseResult.<Boolean>error("档案不存在"));
                    }
                    if (!userId.equals(profile.getUserId())) {
                        return Mono.just(ResponseResult.<Boolean>error("只能删除自己的档案"));
                    }
                    boolean result = healthyUserProfilesService.removeById(id);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.<Boolean>error("删除失败"));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @PutMapping
    @Operation(summary = "修改用户健康档案")
    public ResponseResult<Boolean> update(
            @RequestBody HealthyUserProfiles healthyUserProfiles
    ) {
        boolean result = healthyUserProfilesService.updateById(healthyUserProfiles);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询用户健康档案")
    public ResponseResult<HealthyUserProfiles> getById(
            @Parameter(description = "档案ID") @PathVariable Long id
    ) {
        HealthyUserProfiles healthyUserProfiles = healthyUserProfilesService.getById(id);
        return healthyUserProfiles != null ? ResponseResult.success(healthyUserProfiles) : ResponseResult.error("查询失败");
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的健康档案")
    public Mono<ResponseResult<HealthyUserProfiles>> getMyProfile() {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    HealthyUserProfiles profile = healthyUserProfilesService.getUserProfileByUserId(userId);
                    return profile != null ? ResponseResult.success(profile) : ResponseResult.<HealthyUserProfiles>error("未找到健康档案");
                });
    }

    @PostMapping("/init")
    @Operation(summary = "初始化或更新我的健康档案")
    public Mono<ResponseResult<Boolean>> initOrUpdateProfile(
            @RequestBody HealthyUserProfiles healthyUserProfiles
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    healthyUserProfiles.setUserId(userId);
                    HealthyUserProfiles existingProfile = healthyUserProfilesService.getUserProfileByUserId(userId);
                    boolean result;
                    if (existingProfile != null) {
                        healthyUserProfiles.setId(existingProfile.getId());
                        result = healthyUserProfilesService.updateById(healthyUserProfiles);
                    } else {
                        result = healthyUserProfilesService.save(healthyUserProfiles);
                    }
                    return result ? ResponseResult.success(true) : ResponseResult.error("操作失败");
                });
    }
}
