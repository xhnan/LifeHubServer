package com.xhn.health.dietlogs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.base.utils.SecurityUtils;
import com.xhn.health.dietlogs.model.HealthDietLogs;
import com.xhn.health.dietlogs.service.HealthDietLogsService;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
public class HealthDietLogsController {

    @Autowired
    private HealthDietLogsService healthDietLogsService;

    @PostMapping
    @Operation(summary = "新增饮食日志")
    public Mono<ResponseResult<Boolean>> add(
            @RequestBody HealthDietLogs healthDietLog
    ) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    healthDietLog.setUserId(userId);
                    boolean result = healthDietLogsService.save(healthDietLog);
                    return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除饮食日志")
    public Mono<ResponseResult<Boolean>> delete(
            @Parameter(description = "日志ID") @PathVariable Long id
    ) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .flatMap(userId -> {
                    HealthDietLogs dietLog = healthDietLogsService.getById(id);
                    if (dietLog == null) {
                        return Mono.just(ResponseResult.<Boolean>error("记录不存在"));
                    }
                    if (!userId.equals(dietLog.getUserId())) {
                        return Mono.just(ResponseResult.<Boolean>error("只能删除自己的记录"));
                    }
                    boolean result = healthDietLogsService.removeById(id);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.<Boolean>error("删除失败"));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @PutMapping
    @Operation(summary = "修改饮食日志")
    public ResponseResult<Boolean> update(
            @RequestBody HealthDietLogs healthDietLog
    ) {
        boolean result = healthDietLogsService.updateById(healthDietLog);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询饮食日志")
    public ResponseResult<HealthDietLogs> getById(
            @Parameter(description = "日志ID") @PathVariable Long id
    ) {
        HealthDietLogs healthDietLog = healthDietLogsService.getById(id);
        return healthDietLog != null ? ResponseResult.success(healthDietLog) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有饮食日志列表")
    public ResponseResult<List<HealthDietLogs>> list() {
        List<HealthDietLogs> list = healthDietLogsService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的饮食日志列表")
    public Mono<ResponseResult<List<HealthDietLogs>>> getMyDietLogs(
            @Parameter(description = "用餐类型") @RequestParam(required = false) String mealType
    ) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    List<HealthDietLogs> dietLogs;
                    if (mealType != null && !mealType.isEmpty()) {
                        dietLogs = healthDietLogsService.getDietLogsByUserIdAndMealType(userId, mealType);
                    } else {
                        dietLogs = healthDietLogsService.getDietLogsByUserId(userId);
                    }
                    return ResponseResult.success(dietLogs);
                });
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "根据日期查询我的饮食日志")
    public Mono<ResponseResult<List<HealthDietLogs>>> getDietLogsByDate(
            @Parameter(description = "日期") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    List<HealthDietLogs> dietLogs = healthDietLogsService.getDietLogsByUserIdAndDate(userId, date);
                    return ResponseResult.success(dietLogs);
                });
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询饮食日志")
    public ResponseResult<Page<HealthDietLogs>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        Page<HealthDietLogs> page = new Page<>(pageNum, pageSize);
        Page<HealthDietLogs> resultPage = healthDietLogsService.page(page);
        return ResponseResult.success(resultPage);
    }
}
