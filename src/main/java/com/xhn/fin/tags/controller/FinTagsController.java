package com.xhn.fin.tags.controller;

import com.xhn.fin.tags.model.FinTags;
import com.xhn.fin.tags.service.FinTagsService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FinTags 控制器
 *
 * @author xhn
 * @date 2026-02-04
 */
@RestController
@RequestMapping("/fin/tags")
@Tag(name = "fin", description = "标签管理")
public class FinTagsController {

    @Autowired
    private FinTagsService finTagsService;

    @PostMapping
    @Operation(summary = "新增标签")
    public ResponseResult<Boolean> add(
        @RequestBody FinTags finTags
    ) {
        boolean result = finTagsService.save(finTags);
        return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除标签")
    public ResponseResult<Boolean> delete(
        @Parameter(description = "标签ID") @PathVariable Long id
    ) {
        boolean result = finTagsService.removeById(id);
        return result ? ResponseResult.success(true) : ResponseResult.error("删除失败");
    }

    @PutMapping
    @Operation(summary = "修改标签")
    public ResponseResult<Boolean> update(
        @RequestBody FinTags finTags
    ) {
        boolean result = finTagsService.updateById(finTags);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询标签")
    public ResponseResult<FinTags> getById(
        @Parameter(description = "标签ID") @PathVariable Long id
    ) {
        FinTags finTags = finTagsService.getById(id);
        return finTags != null ? ResponseResult.success(finTags) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有标签列表")
    public ResponseResult<List<FinTags>> list() {
        List<FinTags> list = finTagsService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询标签")
    public ResponseResult<Page<FinTags>> page(
        @Parameter(description = "页码") @RequestParam int pageNum,
        @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        Page<FinTags> page = new Page<>(pageNum, pageSize);
        Page<FinTags> resultPage = finTagsService.page(page);
        return ResponseResult.success(resultPage);
    }
}