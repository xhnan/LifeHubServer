package com.xhn.health.psychology.knowledgebase.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.health.psychology.knowledgebase.model.HealthPsyKnowledgeBase;
import com.xhn.health.psychology.knowledgebase.service.HealthPsyKnowledgeBaseService;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 心理知识库控制器
 *
 * @author xhn
 * @date 2026-03-16
 */
@RestController
@RequestMapping("/health/psychology/knowledge-base")
@Tag(name = "health-psychology", description = "心理健康管理")
public class HealthPsyKnowledgeBaseController {

    @Autowired
    private HealthPsyKnowledgeBaseService healthPsyKnowledgeBaseService;

    @PostMapping
    @Operation(summary = "新增知识库记录")
    public ResponseResult<Boolean> add(
            @RequestBody HealthPsyKnowledgeBase healthPsyKnowledgeBase
    ) {
        boolean result = healthPsyKnowledgeBaseService.save(healthPsyKnowledgeBase);
        return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除知识库记录")
    public ResponseResult<Boolean> delete(
            @Parameter(description = "记录ID") @PathVariable Long id
    ) {
        boolean result = healthPsyKnowledgeBaseService.removeById(id);
        return result ? ResponseResult.success(true) : ResponseResult.error("删除失败");
    }

    @PutMapping
    @Operation(summary = "修改知识库记录")
    public ResponseResult<Boolean> update(
            @RequestBody HealthPsyKnowledgeBase healthPsyKnowledgeBase
    ) {
        boolean result = healthPsyKnowledgeBaseService.updateById(healthPsyKnowledgeBase);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询知识库记录")
    public ResponseResult<HealthPsyKnowledgeBase> getById(
            @Parameter(description = "记录ID") @PathVariable Long id
    ) {
        HealthPsyKnowledgeBase healthPsyKnowledgeBase = healthPsyKnowledgeBaseService.getById(id);
        return healthPsyKnowledgeBase != null ? ResponseResult.success(healthPsyKnowledgeBase) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有知识库记录列表")
    public ResponseResult<List<HealthPsyKnowledgeBase>> list() {
        List<HealthPsyKnowledgeBase> list = healthPsyKnowledgeBaseService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "根据分类查询知识列表")
    public ResponseResult<List<HealthPsyKnowledgeBase>> getByCategory(
            @Parameter(description = "分类") @PathVariable String category
    ) {
        List<HealthPsyKnowledgeBase> knowledgeList = healthPsyKnowledgeBaseService.getKnowledgeByCategory(category);
        return ResponseResult.success(knowledgeList);
    }

    @GetMapping("/search")
    @Operation(summary = "根据标题关键词搜索知识")
    public ResponseResult<List<HealthPsyKnowledgeBase>> searchByTitle(
            @Parameter(description = "标题关键词") @RequestParam String title
    ) {
        List<HealthPsyKnowledgeBase> knowledgeList = healthPsyKnowledgeBaseService.searchKnowledgeByTitle(title);
        return ResponseResult.success(knowledgeList);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询知识库记录")
    public ResponseResult<Page<HealthPsyKnowledgeBase>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        Page<HealthPsyKnowledgeBase> page = new Page<>(pageNum, pageSize);
        Page<HealthPsyKnowledgeBase> resultPage = healthPsyKnowledgeBaseService.page(page);
        return ResponseResult.success(resultPage);
    }
}
