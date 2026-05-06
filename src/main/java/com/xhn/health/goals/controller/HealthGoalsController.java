package com.xhn.health.goals.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.base.utils.SecurityUtils;
import com.xhn.health.goals.model.HealthGoals;
import com.xhn.health.goals.service.HealthGoalsService;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 健康目标控制器
 *
 * @author xhn
 * @date 2026-03-13
 */
@RestController
@RequestMapping("/health/goals")
@Tag(name = "health", description = "健康管理")
public class HealthGoalsController {

    @Autowired
    private HealthGoalsService healthGoalsService;

    @PostMapping
    @Operation(summary = "新增健康目标")
    public Mono<ResponseResult<Boolean>> add(
            @RequestBody HealthGoals healthGoal
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    healthGoal.setUserId(userId);
                    boolean result = healthGoalsService.save(healthGoal);
                    return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除健康目标")
    public Mono<ResponseResult<Boolean>> delete(
            @Parameter(description = "目标ID") @PathVariable Long id
    ) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    HealthGoals goal = healthGoalsService.getById(id);
                    if (goal == null) {
                        return Mono.just(ResponseResult.<Boolean>error("目标不存在"));
                    }
                    if (!userId.equals(goal.getUserId())) {
                        return Mono.just(ResponseResult.<Boolean>error("只能删除自己的目标"));
                    }
                    boolean result = healthGoalsService.removeById(id);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.<Boolean>error("删除失败"));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @PutMapping
    @Operation(summary = "修改健康目标")
    public ResponseResult<Boolean> update(
            @RequestBody HealthGoals healthGoal
    ) {
        boolean result = healthGoalsService.updateById(healthGoal);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询健康目标")
    public ResponseResult<HealthGoals> getById(
            @Parameter(description = "目标ID") @PathVariable Long id
    ) {
        HealthGoals healthGoal = healthGoalsService.getById(id);
        return healthGoal != null ? ResponseResult.success(healthGoal) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有健康目标列表")
    public ResponseResult<List<HealthGoals>> list() {
        List<HealthGoals> list = healthGoalsService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的健康目标列表")
    public Mono<ResponseResult<List<HealthGoals>>> getMyGoals(
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "目标类型") @RequestParam(required = false) String goalType
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    List<HealthGoals> goals;
                    if (status != null && !status.isEmpty()) {
                        goals = healthGoalsService.getGoalsByUserIdAndStatus(userId, status);
                    } else if (goalType != null && !goalType.isEmpty()) {
                        goals = healthGoalsService.getGoalsByUserIdAndType(userId, goalType);
                    } else {
                        goals = healthGoalsService.getGoalsByUserId(userId);
                    }
                    return ResponseResult.success(goals);
                });
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询健康目标")
    public ResponseResult<Page<HealthGoals>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        Page<HealthGoals> page = new Page<>(pageNum, pageSize);
        Page<HealthGoals> resultPage = healthGoalsService.page(page);
        return ResponseResult.success(resultPage);
    }
}
