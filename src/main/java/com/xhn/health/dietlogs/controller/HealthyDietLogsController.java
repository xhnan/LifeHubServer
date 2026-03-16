package com.xhn.health.dietlogs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.base.utils.SecurityUtils;
import com.xhn.health.dietlogs.model.HealthyDietLogs;
import com.xhn.health.dietlogs.service.HealthyDietLogsService;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

/**
 * 饮食日志控制器
 *
 * @author xhn
 * @date 2026-03-13
 */
@RestController
@RequestMapping("/health/diet-logs")
@Tag(name = "health", description = "健康管理")
public class HealthyDietLogsController {

    @Autowired
    private HealthyDietLogsService healthyDietLogsService;

    @PostMapping
    @Operation(summary = "新增饮食日志")
    public Mono<ResponseResult<Boolean>> add(
            @RequestBody HealthyDietLogs healthyDietLogs
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    healthyDietLogs.setUserId(userId);
                    boolean result = healthyDietLogsService.save(healthyDietLogs);
                    return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除饮食日志")
    public Mono<ResponseResult<Boolean>> delete(
            @Parameter(description = "日志ID") @PathVariable Long id
    ) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    HealthyDietLogs dietLog = healthyDietLogsService.getById(id);
                    if (dietLog == null) {
                        return Mono.just(ResponseResult.<Boolean>error("记录不存在"));
                    }
                    if (!userId.equals(dietLog.getUserId())) {
                        return Mono.just(ResponseResult.<Boolean>error("只能删除自己的记录"));
                    }
                    boolean result = healthyDietLogsService.removeById(id);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.<Boolean>error("删除失败"));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @PutMapping
    @Operation(summary = "修改饮食日志")
    public ResponseResult<Boolean> update(
            @RequestBody HealthyDietLogs healthyDietLogs
    ) {
        boolean result = healthyDietLogsService.updateById(healthyDietLogs);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询饮食日志")
    public ResponseResult<HealthyDietLogs> getById(
            @Parameter(description = "日志ID") @PathVariable Long id
    ) {
        HealthyDietLogs healthyDietLogs = healthyDietLogsService.getById(id);
        return healthyDietLogs != null ? ResponseResult.success(healthyDietLogs) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有饮食日志列表")
    public ResponseResult<List<HealthyDietLogs>> list() {
        List<HealthyDietLogs> list = healthyDietLogsService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的饮食日志列表")
    public Mono<ResponseResult<List<HealthyDietLogs>>> getMyDietLogs(
            @Parameter(description = "用餐类型") @RequestParam(required = false) String mealType
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    List<HealthyDietLogs> dietLogs;
                    if (mealType != null && !mealType.isEmpty()) {
                        dietLogs = healthyDietLogsService.getDietLogsByUserIdAndMealType(userId, mealType);
                    } else {
                        dietLogs = healthyDietLogsService.getDietLogsByUserId(userId);
                    }
                    return ResponseResult.success(dietLogs);
                });
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "根据日期查询我的饮食日志")
    public Mono<ResponseResult<List<HealthyDietLogs>>> getDietLogsByDate(
            @Parameter(description = "日期") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    List<HealthyDietLogs> dietLogs = healthyDietLogsService.getDietLogsByUserIdAndDate(userId, date);
                    return ResponseResult.success(dietLogs);
                });
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询饮食日志")
    public ResponseResult<Page<HealthyDietLogs>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        Page<HealthyDietLogs> page = new Page<>(pageNum, pageSize);
        Page<HealthyDietLogs> resultPage = healthyDietLogsService.page(page);
        return ResponseResult.success(resultPage);
    }
}
