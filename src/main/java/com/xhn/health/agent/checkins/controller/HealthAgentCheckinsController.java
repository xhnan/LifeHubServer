package com.xhn.health.agent.checkins.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.base.utils.SecurityUtils;
import com.xhn.health.agent.checkins.model.HealthAgentCheckins;
import com.xhn.health.agent.checkins.service.HealthAgentCheckinsService;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/health/agent/checkins")
@Tag(name = "health-agent", description = "Health agent data")
public class HealthAgentCheckinsController {

    @Autowired
    private HealthAgentCheckinsService healthAgentCheckinsService;

    @PostMapping
    @Operation(summary = "Create checkin")
    public Mono<ResponseResult<Boolean>> add(@RequestBody HealthAgentCheckins checkin) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    checkin.setUserId(userId);
                    boolean result = healthAgentCheckinsService.save(checkin);
                    return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete checkin")
    public Mono<ResponseResult<Boolean>> delete(@Parameter(description = "ID") @PathVariable Long id) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    HealthAgentCheckins checkin = healthAgentCheckinsService.getById(id);
                    if (checkin == null) {
                        return Mono.just(ResponseResult.<Boolean>error("记录不存在"));
                    }
                    if (!userId.equals(checkin.getUserId())) {
                        return Mono.just(ResponseResult.<Boolean>error("无权删除该记录"));
                    }
                    boolean result = healthAgentCheckinsService.removeById(id);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.<Boolean>error("删除失败"));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @PutMapping
    @Operation(summary = "Update checkin")
    public ResponseResult<Boolean> update(@RequestBody HealthAgentCheckins checkin) {
        boolean result = healthAgentCheckinsService.updateById(checkin);
        return result ? ResponseResult.success(true) : ResponseResult.error("更新失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get checkin by id")
    public ResponseResult<HealthAgentCheckins> getById(@Parameter(description = "ID") @PathVariable Long id) {
        HealthAgentCheckins checkin = healthAgentCheckinsService.getById(id);
        return checkin != null ? ResponseResult.success(checkin) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "List checkins")
    public ResponseResult<List<HealthAgentCheckins>> list() {
        return ResponseResult.success(healthAgentCheckinsService.list());
    }

    @GetMapping("/my")
    @Operation(summary = "Get my checkins")
    public Mono<ResponseResult<List<HealthAgentCheckins>>> my(@RequestParam(required = false) Long followupPlanId) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    List<HealthAgentCheckins> list = followupPlanId != null
                            ? healthAgentCheckinsService.getCheckinsByFollowupPlanId(followupPlanId)
                            : healthAgentCheckinsService.getCheckinsByUserId(userId);
                    return ResponseResult.success(list);
                });
    }

    @GetMapping("/page")
    @Operation(summary = "Page checkins")
    public ResponseResult<Page<HealthAgentCheckins>> page(@RequestParam int pageNum, @RequestParam int pageSize) {
        Page<HealthAgentCheckins> page = new Page<>(pageNum, pageSize);
        return ResponseResult.success(healthAgentCheckinsService.page(page));
    }
}
