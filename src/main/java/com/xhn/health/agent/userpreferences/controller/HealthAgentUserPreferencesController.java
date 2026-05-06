package com.xhn.health.agent.userpreferences.controller;

import com.xhn.base.utils.SecurityUtils;
import com.xhn.health.agent.userpreferences.model.HealthAgentUserPreferences;
import com.xhn.health.agent.userpreferences.service.HealthAgentUserPreferencesService;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RestController
@RequestMapping("/health/agent/user-preferences")
@Tag(name = "health-agent", description = "Health agent data")
public class HealthAgentUserPreferencesController {

    @Autowired
    private HealthAgentUserPreferencesService healthAgentUserPreferencesService;

    @PostMapping
    @Operation(summary = "Create user preferences")
    public Mono<ResponseResult<Boolean>> add(@RequestBody HealthAgentUserPreferences preferences) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    preferences.setUserId(userId);
                    boolean result = healthAgentUserPreferencesService.save(preferences);
                    return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user preferences")
    public Mono<ResponseResult<Boolean>> delete(@Parameter(description = "ID") @PathVariable Long id) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .flatMap(userId -> {
                    HealthAgentUserPreferences preferences = healthAgentUserPreferencesService.getById(id);
                    if (preferences == null) {
                        return Mono.just(ResponseResult.<Boolean>error("记录不存在"));
                    }
                    if (!userId.equals(preferences.getUserId())) {
                        return Mono.just(ResponseResult.<Boolean>error("无权删除该记录"));
                    }
                    boolean result = healthAgentUserPreferencesService.removeById(id);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.<Boolean>error("删除失败"));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @PutMapping
    @Operation(summary = "Update user preferences")
    public ResponseResult<Boolean> update(@RequestBody HealthAgentUserPreferences preferences) {
        boolean result = healthAgentUserPreferencesService.updateById(preferences);
        return result ? ResponseResult.success(true) : ResponseResult.error("更新失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user preferences by id")
    public ResponseResult<HealthAgentUserPreferences> getById(@Parameter(description = "ID") @PathVariable Long id) {
        HealthAgentUserPreferences preferences = healthAgentUserPreferencesService.getById(id);
        return preferences != null ? ResponseResult.success(preferences) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "List user preferences")
    public ResponseResult<List<HealthAgentUserPreferences>> list() {
        return ResponseResult.success(healthAgentUserPreferencesService.list());
    }

    @GetMapping("/my")
    @Operation(summary = "Get my user preferences")
    public Mono<ResponseResult<HealthAgentUserPreferences>> my() {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    HealthAgentUserPreferences preferences = healthAgentUserPreferencesService.getByUserId(userId);
                    return preferences != null ? ResponseResult.success(preferences) : ResponseResult.<HealthAgentUserPreferences>error("未找到记录");
                });
    }
}
