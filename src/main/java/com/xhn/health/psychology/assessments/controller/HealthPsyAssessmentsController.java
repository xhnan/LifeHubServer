package com.xhn.health.psychology.assessments.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.base.utils.SecurityUtils;
import com.xhn.health.psychology.assessments.model.HealthPsyAssessments;
import com.xhn.health.psychology.assessments.service.HealthPsyAssessmentsService;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

/**
 * 心理评估控制器
 *
 * @author xhn
 * @date 2026-03-16
 */
@RestController
@RequestMapping("/health/psychology/assessments")
@Tag(name = "health-psychology", description = "心理健康管理")
public class HealthPsyAssessmentsController {

    @Autowired
    private HealthPsyAssessmentsService healthPsyAssessmentsService;

    @PostMapping
    @Operation(summary = "新增心理评估记录")
    public Mono<ResponseResult<Boolean>> add(
            @RequestBody HealthPsyAssessments healthPsyAssessments
    ) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    healthPsyAssessments.setUserId(userId);
                    boolean result = healthPsyAssessmentsService.save(healthPsyAssessments);
                    return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除心理评估记录")
    public Mono<ResponseResult<Boolean>> delete(
            @Parameter(description = "评估记录ID") @PathVariable Long id
    ) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .flatMap(userId -> {
                    HealthPsyAssessments assessment = healthPsyAssessmentsService.getById(id);
                    if (assessment == null) {
                        return Mono.just(ResponseResult.<Boolean>error("记录不存在"));
                    }
                    if (!userId.equals(assessment.getUserId())) {
                        return Mono.just(ResponseResult.<Boolean>error("只能删除自己的记录"));
                    }
                    boolean result = healthPsyAssessmentsService.removeById(id);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.<Boolean>error("删除失败"));
                })
                .switchIfEmpty(Mono.just(ResponseResult.error("用户未登录")));
    }

    @PutMapping
    @Operation(summary = "修改心理评估记录")
    public ResponseResult<Boolean> update(
            @RequestBody HealthPsyAssessments healthPsyAssessments
    ) {
        boolean result = healthPsyAssessmentsService.updateById(healthPsyAssessments);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询心理评估记录")
    public ResponseResult<HealthPsyAssessments> getById(
            @Parameter(description = "评估记录ID") @PathVariable Long id
    ) {
        HealthPsyAssessments healthPsyAssessments = healthPsyAssessmentsService.getById(id);
        return healthPsyAssessments != null ? ResponseResult.success(healthPsyAssessments) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有心理评估记录列表")
    public ResponseResult<List<HealthPsyAssessments>> list() {
        List<HealthPsyAssessments> list = healthPsyAssessmentsService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的心理评估记录列表")
    public Mono<ResponseResult<List<HealthPsyAssessments>>> getMyAssessments(
            @Parameter(description = "量表名称") @RequestParam(required = false) String scaleName
    ) {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    List<HealthPsyAssessments> assessments;
                    if (scaleName != null && !scaleName.isEmpty()) {
                        assessments = healthPsyAssessmentsService.getAssessmentsByUserIdAndScaleName(userId, scaleName);
                    } else {
                        assessments = healthPsyAssessmentsService.getAssessmentsByUserId(userId);
                    }
                    return ResponseResult.success(assessments);
                });
    }

    @GetMapping("/latest")
    @Operation(summary = "获取我的最新心理评估记录")
    public Mono<ResponseResult<HealthPsyAssessments>> getLatestAssessment() {
        return SecurityUtils.getCurrentUserId()
                .publishOn(Schedulers.boundedElastic())
                .map(userId -> {
                    HealthPsyAssessments assessment = healthPsyAssessmentsService.getLatestAssessmentByUserId(userId);
                    return assessment != null ? ResponseResult.success(assessment) : ResponseResult.<HealthPsyAssessments>error("未找到评估记录");
                });
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询心理评估记录")
    public ResponseResult<Page<HealthPsyAssessments>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        Page<HealthPsyAssessments> page = new Page<>(pageNum, pageSize);
        Page<HealthPsyAssessments> resultPage = healthPsyAssessmentsService.page(page);
        return ResponseResult.success(resultPage);
    }
}
