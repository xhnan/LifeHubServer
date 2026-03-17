package com.xhn.health.psychology.dailymoods.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.base.utils.SecurityUtils;
import com.xhn.health.psychology.dailymoods.model.HealthPsyDailyMoods;
import com.xhn.health.psychology.dailymoods.service.HealthPsyDailyMoodsService;
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
 * 每日心情记录控制器
 *
 * @author xhn
 * @date 2026-03-16
 */
@RestController
@RequestMapping("/health/psychology/daily-moods")
@Tag(name = "health-psychology", description = "心理健康管理")
public class HealthPsyDailyMoodsController {

    @Autowired
    private HealthPsyDailyMoodsService healthPsyDailyMoodsService;

    @PostMapping
    @Operation(summary = "新增心情记录")
    public Mono<ResponseResult<Boolean>> add(
            @RequestBody HealthPsyDailyMoods healthPsyDailyMoods
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    healthPsyDailyMoods.setUserId(userId);
                    boolean result = healthPsyDailyMoodsService.save(healthPsyDailyMoods);
                    return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除心情记录")
    public Mono<ResponseResult<Boolean>> delete(
            @Parameter(description = "记录ID") @PathVariable Long id
    ) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    HealthPsyDailyMoods mood = healthPsyDailyMoodsService.getById(id);
                    if (mood == null) {
                        return Mono.just(ResponseResult.<Boolean>error("记录不存在"));
                    }
                    if (!userId.equals(mood.getUserId())) {
                        return Mono.just(ResponseResult.<Boolean>error("只能删除自己的记录"));
                    }
                    boolean result = healthPsyDailyMoodsService.removeById(id);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.<Boolean>error("删除失败"));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @PutMapping
    @Operation(summary = "修改心情记录")
    public ResponseResult<Boolean> update(
            @RequestBody HealthPsyDailyMoods healthPsyDailyMoods
    ) {
        boolean result = healthPsyDailyMoodsService.updateById(healthPsyDailyMoods);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询心情记录")
    public ResponseResult<HealthPsyDailyMoods> getById(
            @Parameter(description = "记录ID") @PathVariable Long id
    ) {
        HealthPsyDailyMoods healthPsyDailyMoods = healthPsyDailyMoodsService.getById(id);
        return healthPsyDailyMoods != null ? ResponseResult.success(healthPsyDailyMoods) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有心情记录列表")
    public ResponseResult<List<HealthPsyDailyMoods>> list() {
        List<HealthPsyDailyMoods> list = healthPsyDailyMoodsService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的心情记录列表")
    public Mono<ResponseResult<List<HealthPsyDailyMoods>>> getMyMoods() {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    List<HealthPsyDailyMoods> moods = healthPsyDailyMoodsService.getMoodsByUserId(userId);
                    return ResponseResult.success(moods);
                });
    }

    @GetMapping("/latest")
    @Operation(summary = "获取我的最新心情记录")
    public Mono<ResponseResult<HealthPsyDailyMoods>> getLatestMood() {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    HealthPsyDailyMoods mood = healthPsyDailyMoodsService.getLatestMoodByUserId(userId);
                    return mood != null ? ResponseResult.success(mood) : ResponseResult.<HealthPsyDailyMoods>error("未找到心情记录");
                });
    }

    @GetMapping("/date/{recordDate}")
    @Operation(summary = "根据日期查询我的心情记录")
    public Mono<ResponseResult<HealthPsyDailyMoods>> getMoodByDate(
            @Parameter(description = "记录日期") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate recordDate
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    HealthPsyDailyMoods mood = healthPsyDailyMoodsService.getMoodByUserIdAndDate(userId, recordDate);
                    return mood != null ? ResponseResult.success(mood) : ResponseResult.<HealthPsyDailyMoods>error("未找到该日期的记录");
                });
    }

    @GetMapping("/range")
    @Operation(summary = "根据日期范围查询我的心情记录")
    public Mono<ResponseResult<List<HealthPsyDailyMoods>>> getMoodsByRange(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    List<HealthPsyDailyMoods> moods = healthPsyDailyMoodsService.getMoodsByUserIdAndDateRange(userId, startDate, endDate);
                    return ResponseResult.success(moods);
                });
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询心情记录")
    public ResponseResult<Page<HealthPsyDailyMoods>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        Page<HealthPsyDailyMoods> page = new Page<>(pageNum, pageSize);
        Page<HealthPsyDailyMoods> resultPage = healthPsyDailyMoodsService.page(page);
        return ResponseResult.success(resultPage);
    }
}
