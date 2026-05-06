package com.xhn.health.dailysummaries.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.base.utils.SecurityUtils;
import com.xhn.health.dailysummaries.model.HealthDailySummaries;
import com.xhn.health.dailysummaries.service.HealthDailySummariesService;
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
 * 每日健康汇总控制器
 *
 * @author xhn
 * @date 2026-03-13
 */
@RestController
@RequestMapping("/health/daily-summaries")
@Tag(name = "health", description = "健康管理")
public class HealthDailySummariesController {

    @Autowired
    private HealthDailySummariesService healthDailySummariesService;

    @PostMapping
    @Operation(summary = "新增每日健康汇总")
    public Mono<ResponseResult<Boolean>> add(
            @RequestBody HealthDailySummaries healthDailySummary
    ) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    healthDailySummary.setUserId(userId);
                    boolean result = healthDailySummariesService.save(healthDailySummary);
                    return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除每日健康汇总")
    public Mono<ResponseResult<Boolean>> delete(
            @Parameter(description = "汇总ID") @PathVariable Long id
    ) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .flatMap(userId -> {
                    HealthDailySummaries summary = healthDailySummariesService.getById(id);
                    if (summary == null) {
                        return Mono.just(ResponseResult.<Boolean>error("记录不存在"));
                    }
                    if (!userId.equals(summary.getUserId())) {
                        return Mono.just(ResponseResult.<Boolean>error("只能删除自己的记录"));
                    }
                    boolean result = healthDailySummariesService.removeById(id);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.<Boolean>error("删除失败"));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @PutMapping
    @Operation(summary = "修改每日健康汇总")
    public ResponseResult<Boolean> update(
            @RequestBody HealthDailySummaries healthDailySummary
    ) {
        boolean result = healthDailySummariesService.updateById(healthDailySummary);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询每日健康汇总")
    public ResponseResult<HealthDailySummaries> getById(
            @Parameter(description = "汇总ID") @PathVariable Long id
    ) {
        HealthDailySummaries healthDailySummary = healthDailySummariesService.getById(id);
        return healthDailySummary != null ? ResponseResult.success(healthDailySummary) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有每日健康汇总列表")
    public ResponseResult<List<HealthDailySummaries>> list() {
        List<HealthDailySummaries> list = healthDailySummariesService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的每日健康汇总列表")
    public Mono<ResponseResult<List<HealthDailySummaries>>> getMySummaries() {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    List<HealthDailySummaries> summaries = healthDailySummariesService.getSummariesByUserId(userId);
                    return ResponseResult.success(summaries);
                });
    }

    @GetMapping("/date/{recordDate}")
    @Operation(summary = "根据日期查询我的每日健康汇总")
    public Mono<ResponseResult<HealthDailySummaries>> getSummaryByDate(
            @Parameter(description = "记录日期") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate recordDate
    ) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    HealthDailySummaries summary = healthDailySummariesService.getSummaryByUserIdAndDate(userId, recordDate);
                    return summary != null ? ResponseResult.success(summary) : ResponseResult.<HealthDailySummaries>error("未找到该日期的记录");
                });
    }

    @GetMapping("/range")
    @Operation(summary = "根据日期范围查询我的每日健康汇总")
    public Mono<ResponseResult<List<HealthDailySummaries>>> getSummariesByRange(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    List<HealthDailySummaries> summaries = healthDailySummariesService.getSummariesByUserIdAndDateRange(userId, startDate, endDate);
                    return ResponseResult.success(summaries);
                });
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询每日健康汇总")
    public ResponseResult<Page<HealthDailySummaries>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        Page<HealthDailySummaries> page = new Page<>(pageNum, pageSize);
        Page<HealthDailySummaries> resultPage = healthDailySummariesService.page(page);
        return ResponseResult.success(resultPage);
    }
}
