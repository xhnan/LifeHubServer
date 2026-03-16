package com.xhn.health.activities.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.base.utils.SecurityUtils;
import com.xhn.health.activities.model.HealthyActivities;
import com.xhn.health.activities.service.HealthyActivitiesService;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 健康活动控制器
 *
 * @author xhn
 * @date 2026-03-13
 */
@RestController
@RequestMapping("/health/activities")
@Tag(name = "health", description = "健康管理")
public class HealthyActivitiesController {

    @Autowired
    private HealthyActivitiesService healthyActivitiesService;

    @PostMapping
    @Operation(summary = "新增健康活动")
    public Mono<ResponseResult<Boolean>> add(
            @RequestBody HealthyActivities healthyActivities
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    healthyActivities.setUserId(userId);
                    boolean result = healthyActivitiesService.save(healthyActivities);
                    return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除健康活动")
    public Mono<ResponseResult<Boolean>> delete(
            @Parameter(description = "活动ID") @PathVariable Long id
    ) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    HealthyActivities activity = healthyActivitiesService.getById(id);
                    if (activity == null) {
                        return Mono.just(ResponseResult.<Boolean>error("活动不存在"));
                    }
                    if (!userId.equals(activity.getUserId())) {
                        return Mono.just(ResponseResult.<Boolean>error("只能删除自己的活动记录"));
                    }
                    boolean result = healthyActivitiesService.removeById(id);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.<Boolean>error("删除失败"));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @PutMapping
    @Operation(summary = "修改健康活动")
    public ResponseResult<Boolean> update(
            @RequestBody HealthyActivities healthyActivities
    ) {
        boolean result = healthyActivitiesService.updateById(healthyActivities);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询健康活动")
    public ResponseResult<HealthyActivities> getById(
            @Parameter(description = "活动ID") @PathVariable Long id
    ) {
        HealthyActivities healthyActivities = healthyActivitiesService.getById(id);
        return healthyActivities != null ? ResponseResult.success(healthyActivities) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有健康活动列表")
    public ResponseResult<List<HealthyActivities>> list() {
        List<HealthyActivities> list = healthyActivitiesService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的健康活动列表")
    public Mono<ResponseResult<List<HealthyActivities>>> getMyActivities(
            @Parameter(description = "活动类型") @RequestParam(required = false) String activityType
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    List<HealthyActivities> activities;
                    if (activityType != null && !activityType.isEmpty()) {
                        activities = healthyActivitiesService.getActivitiesByUserIdAndType(userId, activityType);
                    } else {
                        activities = healthyActivitiesService.getActivitiesByUserId(userId);
                    }
                    return ResponseResult.success(activities);
                });
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询健康活动")
    public ResponseResult<Page<HealthyActivities>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        Page<HealthyActivities> page = new Page<>(pageNum, pageSize);
        Page<HealthyActivities> resultPage = healthyActivitiesService.page(page);
        return ResponseResult.success(resultPage);
    }
}
