package com.xhn.fin.trans_tags.controller;

import com.xhn.fin.trans_tags.model.FinTransTags;
import com.xhn.fin.trans_tags.service.FinTransTagsService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FinTransTags 控制器
 *
 * @author xhn
 * @date 2026-02-04
 */
@RestController
@RequestMapping("/fin/transtags")
@Tag(name = "fin", description = "交易标签管理")
public class FinTransTagsController {

    @Autowired
    private FinTransTagsService finTransTagsService;

    @PostMapping
    @Operation(summary = "新增交易标签")
    public ResponseResult<Boolean> add(
        @RequestBody FinTransTags finTransTags
    ) {
        boolean result = finTransTagsService.save(finTransTags);
        return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除交易标签")
    public ResponseResult<Boolean> delete(
        @Parameter(description = "标签ID") @PathVariable Long id
    ) {
        boolean result = finTransTagsService.removeById(id);
        return result ? ResponseResult.success(true) : ResponseResult.error("删除失败");
    }

    @PutMapping
    @Operation(summary = "修改交易标签")
    public ResponseResult<Boolean> update(
        @RequestBody FinTransTags finTransTags
    ) {
        boolean result = finTransTagsService.updateById(finTransTags);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询交易标签")
    public ResponseResult<FinTransTags> getById(
        @Parameter(description = "标签ID") @PathVariable Long id
    ) {
        FinTransTags finTransTags = finTransTagsService.getById(id);
        return finTransTags != null ? ResponseResult.success(finTransTags) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有交易标签列表")
    public ResponseResult<List<FinTransTags>> list() {
        List<FinTransTags> list = finTransTagsService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询交易标签")
    public ResponseResult<Page<FinTransTags>> page(
        @Parameter(description = "页码") @RequestParam int pageNum,
        @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        Page<FinTransTags> page = new Page<>(pageNum, pageSize);
        Page<FinTransTags> resultPage = finTransTagsService.page(page);
        return ResponseResult.success(resultPage);
    }
}