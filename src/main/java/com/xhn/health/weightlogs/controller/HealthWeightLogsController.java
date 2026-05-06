package com.xhn.health.weightlogs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.base.utils.SecurityUtils;
import com.xhn.health.weightlogs.model.HealthWeightLogs;
import com.xhn.health.weightlogs.service.HealthWeightLogsService;
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
 * 体重记录控制器
 *
 * @author xhn
 * @date 2026-03-13
 */
@RestController
@RequestMapping("/health/weight-logs")
@Tag(name = "health", description = "健康管理")
public class HealthWeightLogsController {

    @Autowired
    private HealthWeightLogsService healthWeightLogsService;

    @PostMapping
    @Operation(summary = "新增体重记录")
    public Mono<ResponseResult<Boolean>> add(
            @RequestBody HealthWeightLogs healthWeightLog
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    healthWeightLog.setUserId(userId);
                    boolean result = healthWeightLogsService.save(healthWeightLog);
                    return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除体重记录")
    public Mono<ResponseResult<Boolean>> delete(
            @Parameter(description = "记录ID") @PathVariable Long id
    ) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    HealthWeightLogs weightLog = healthWeightLogsService.getById(id);
                    if (weightLog == null) {
                        return Mono.just(ResponseResult.<Boolean>error("记录不存在"));
                    }
                    if (!userId.equals(weightLog.getUserId())) {
                        return Mono.just(ResponseResult.<Boolean>error("只能删除自己的记录"));
                    }
                    boolean result = healthWeightLogsService.removeById(id);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.<Boolean>error("删除失败"));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @PutMapping
    @Operation(summary = "修改体重记录")
    public ResponseResult<Boolean> update(
            @RequestBody HealthWeightLogs healthWeightLog
    ) {
        boolean result = healthWeightLogsService.updateById(healthWeightLog);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询体重记录")
    public ResponseResult<HealthWeightLogs> getById(
            @Parameter(description = "记录ID") @PathVariable Long id
    ) {
        HealthWeightLogs healthWeightLog = healthWeightLogsService.getById(id);
        return healthWeightLog != null ? ResponseResult.success(healthWeightLog) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有体重记录列表")
    public ResponseResult<List<HealthWeightLogs>> list() {
        List<HealthWeightLogs> list = healthWeightLogsService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的体重记录列表")
    public Mono<ResponseResult<List<HealthWeightLogs>>> getMyWeightLogs() {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    List<HealthWeightLogs> weightLogs = healthWeightLogsService.getWeightLogsByUserId(userId);
                    return ResponseResult.success(weightLogs);
                });
    }

    @GetMapping("/latest")
    @Operation(summary = "获取我的最新体重记录")
    public Mono<ResponseResult<HealthWeightLogs>> getLatestWeightLog() {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    HealthWeightLogs weightLog = healthWeightLogsService.getLatestWeightLogByUserId(userId);
                    return weightLog != null ? ResponseResult.success(weightLog) : ResponseResult.<HealthWeightLogs>error("未找到体重记录");
                });
    }

    @GetMapping("/date/{recordDate}")
    @Operation(summary = "根据日期查询我的体重记录")
    public Mono<ResponseResult<HealthWeightLogs>> getWeightLogByDate(
            @Parameter(description = "记录日期") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate recordDate
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    HealthWeightLogs weightLog = healthWeightLogsService.getWeightLogByUserIdAndDate(userId, recordDate);
                    return weightLog != null ? ResponseResult.success(weightLog) : ResponseResult.<HealthWeightLogs>error("未找到该日期的记录");
                });
    }

    @GetMapping("/range")
    @Operation(summary = "根据日期范围查询我的体重记录")
    public Mono<ResponseResult<List<HealthWeightLogs>>> getWeightLogsByRange(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    List<HealthWeightLogs> weightLogs = healthWeightLogsService.getWeightLogsByUserIdAndDateRange(userId, startDate, endDate);
                    return ResponseResult.success(weightLogs);
                });
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询体重记录")
    public ResponseResult<Page<HealthWeightLogs>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        Page<HealthWeightLogs> page = new Page<>(pageNum, pageSize);
        Page<HealthWeightLogs> resultPage = healthWeightLogsService.page(page);
        return ResponseResult.success(resultPage);
    }
}
