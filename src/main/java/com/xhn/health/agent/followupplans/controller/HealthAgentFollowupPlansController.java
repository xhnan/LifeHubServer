package com.xhn.health.agent.followupplans.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.base.utils.SecurityUtils;
import com.xhn.health.agent.followupplans.model.HealthAgentFollowupPlans;
import com.xhn.health.agent.followupplans.service.HealthAgentFollowupPlansService;
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
@RequestMapping("/health/agent/followup-plans")
@Tag(name = "health-agent", description = "Health agent data")
public class HealthAgentFollowupPlansController {

    @Autowired
    private HealthAgentFollowupPlansService healthAgentFollowupPlansService;

    @PostMapping
    @Operation(summary = "Create followup plan")
    public Mono<ResponseResult<Boolean>> add(@RequestBody HealthAgentFollowupPlans followupPlan) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    followupPlan.setUserId(userId);
                    boolean result = healthAgentFollowupPlansService.save(followupPlan);
                    return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete followup plan")
    public Mono<ResponseResult<Boolean>> delete(@Parameter(description = "ID") @PathVariable Long id) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .flatMap(userId -> {
                    HealthAgentFollowupPlans followupPlan = healthAgentFollowupPlansService.getById(id);
                    if (followupPlan == null) {
                        return Mono.just(ResponseResult.<Boolean>error("记录不存在"));
                    }
                    if (!userId.equals(followupPlan.getUserId())) {
                        return Mono.just(ResponseResult.<Boolean>error("无权删除该记录"));
                    }
                    boolean result = healthAgentFollowupPlansService.removeById(id);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.<Boolean>error("删除失败"));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @PutMapping
    @Operation(summary = "Update followup plan")
    public ResponseResult<Boolean> update(@RequestBody HealthAgentFollowupPlans followupPlan) {
        boolean result = healthAgentFollowupPlansService.updateById(followupPlan);
        return result ? ResponseResult.success(true) : ResponseResult.error("更新失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get followup plan by id")
    public ResponseResult<HealthAgentFollowupPlans> getById(@Parameter(description = "ID") @PathVariable Long id) {
        HealthAgentFollowupPlans followupPlan = healthAgentFollowupPlansService.getById(id);
        return followupPlan != null ? ResponseResult.success(followupPlan) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "List followup plans")
    public ResponseResult<List<HealthAgentFollowupPlans>> list() {
        return ResponseResult.success(healthAgentFollowupPlansService.list());
    }

    @GetMapping("/my")
    @Operation(summary = "Get my followup plans")
    public Mono<ResponseResult<List<HealthAgentFollowupPlans>>> my(@RequestParam(required = false) Boolean activeOnly) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    List<HealthAgentFollowupPlans> list = Boolean.TRUE.equals(activeOnly)
                            ? healthAgentFollowupPlansService.getActiveFollowupPlansByUserId(userId)
                            : healthAgentFollowupPlansService.getFollowupPlansByUserId(userId);
                    return ResponseResult.success(list);
                });
    }

    @GetMapping("/page")
    @Operation(summary = "Page followup plans")
    public ResponseResult<Page<HealthAgentFollowupPlans>> page(@RequestParam int pageNum, @RequestParam int pageSize) {
        Page<HealthAgentFollowupPlans> page = new Page<>(pageNum, pageSize);
        return ResponseResult.success(healthAgentFollowupPlansService.page(page));
    }
}
