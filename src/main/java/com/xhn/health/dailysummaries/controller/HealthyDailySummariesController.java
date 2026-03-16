package com.xhn.health.dailysummaries.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.base.utils.SecurityUtils;
import com.xhn.health.dailysummaries.model.HealthyDailySummaries;
import com.xhn.health.dailysummaries.service.HealthyDailySummariesService;
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
 * 每日健康汇总控制器
 *
 * @author xhn
 * @date 2026-03-13
 */
@RestController
@RequestMapping("/health/daily-summaries")
@Tag(name = "health", description = "健康管理")
public class HealthyDailySummariesController {

    @Autowired
    private HealthyDailySummariesService healthyDailySummariesService;

    @PostMapping
    @Operation(summary = "新增每日健康汇总")
    public Mono<ResponseResult<Boolean>> add(
            @RequestBody HealthyDailySummaries healthyDailySummaries
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    healthyDailySummaries.setUserId(userId);
                    boolean result = healthyDailySummariesService.save(healthyDailySummaries);
                    return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除每日健康汇总")
    public Mono<ResponseResult<Boolean>> delete(
            @Parameter(description = "汇总ID") @PathVariable Long id
    ) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    HealthyDailySummaries summary = healthyDailySummariesService.getById(id);
                    if (summary == null) {
                        return Mono.just(ResponseResult.<Boolean>error("记录不存在"));
                    }
                    if (!userId.equals(summary.getUserId())) {
                        return Mono.just(ResponseResult.<Boolean>error("只能删除自己的记录"));
                    }
                    boolean result = healthyDailySummariesService.removeById(id);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.<Boolean>error("删除失败"));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @PutMapping
    @Operation(summary = "修改每日健康汇总")
    public ResponseResult<Boolean> update(
            @RequestBody HealthyDailySummaries healthyDailySummaries
    ) {
        boolean result = healthyDailySummariesService.updateById(healthyDailySummaries);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询每日健康汇总")
    public ResponseResult<HealthyDailySummaries> getById(
            @Parameter(description = "汇总ID") @PathVariable Long id
    ) {
        HealthyDailySummaries healthyDailySummaries = healthyDailySummariesService.getById(id);
        return healthyDailySummaries != null ? ResponseResult.success(healthyDailySummaries) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有每日健康汇总列表")
    public ResponseResult<List<HealthyDailySummaries>> list() {
        List<HealthyDailySummaries> list = healthyDailySummariesService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的每日健康汇总列表")
    public Mono<ResponseResult<List<HealthyDailySummaries>>> getMySummaries() {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    List<HealthyDailySummaries> summaries = healthyDailySummariesService.getSummariesByUserId(userId);
                    return ResponseResult.success(summaries);
                });
    }

    @GetMapping("/date/{recordDate}")
    @Operation(summary = "根据日期查询我的每日健康汇总")
    public Mono<ResponseResult<HealthyDailySummaries>> getSummaryByDate(
            @Parameter(description = "记录日期") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate recordDate
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    HealthyDailySummaries summary = healthyDailySummariesService.getSummaryByUserIdAndDate(userId, recordDate);
                    return summary != null ? ResponseResult.success(summary) : ResponseResult.<HealthyDailySummaries>error("未找到该日期的记录");
                });
    }

    @GetMapping("/range")
    @Operation(summary = "根据日期范围查询我的每日健康汇总")
    public Mono<ResponseResult<List<HealthyDailySummaries>>> getSummariesByRange(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    List<HealthyDailySummaries> summaries = healthyDailySummariesService.getSummariesByUserIdAndDateRange(userId, startDate, endDate);
                    return ResponseResult.success(summaries);
                });
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询每日健康汇总")
    public ResponseResult<Page<HealthyDailySummaries>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        Page<HealthyDailySummaries> page = new Page<>(pageNum, pageSize);
        Page<HealthyDailySummaries> resultPage = healthyDailySummariesService.page(page);
        return ResponseResult.success(resultPage);
    }
}
