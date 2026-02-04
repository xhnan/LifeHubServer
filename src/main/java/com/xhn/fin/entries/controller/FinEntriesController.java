package com.xhn.fin.entries.controller;

import com.xhn.fin.entries.model.FinEntries;
import com.xhn.fin.entries.service.FinEntriesService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FinEntries 控制器
 *
 * @author xhn
 * @date 2026-02-04
 */
@RestController
@RequestMapping("/fin/entries")
@Tag(name = "fin", description = "财务分录管理")
public class FinEntriesController {

    @Autowired
    private FinEntriesService finEntriesService;

    @PostMapping
    @Operation(summary = "新增财务分录")
    public ResponseResult<Boolean> add(
            @RequestBody FinEntries finEntries
    ) {
        boolean result = finEntriesService.save(finEntries);
        return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除财务分录")
    public ResponseResult<Boolean> delete(
            @Parameter(description = "分录ID") @PathVariable Long id
    ) {
        boolean result = finEntriesService.removeById(id);
        return result ? ResponseResult.success(true) : ResponseResult.error("删除失败");
    }

    @PutMapping
    @Operation(summary = "修改财务分录")
    public ResponseResult<Boolean> update(
            @RequestBody FinEntries finEntries
    ) {
        boolean result = finEntriesService.updateById(finEntries);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询财务分录")
    public ResponseResult<FinEntries> getById(
            @Parameter(description = "分录ID") @PathVariable Long id
    ) {
        FinEntries finEntries = finEntriesService.getById(id);
        return finEntries != null ? ResponseResult.success(finEntries) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有财务分录列表")
    public ResponseResult<List<FinEntries>> list() {
        List<FinEntries> list = finEntriesService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询财务分录")
    public ResponseResult<Page<FinEntries>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        Page<FinEntries> page = new Page<>(pageNum, pageSize);
        Page<FinEntries> resultPage = finEntriesService.page(page);
        return ResponseResult.success(resultPage);
    }
}