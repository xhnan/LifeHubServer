package com.xhn.fin.entries.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.fin.entries.model.FinEntries;
import com.xhn.fin.entries.service.FinEntriesService;
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
            @Parameter(description = "分录ID") @PathVariable Long id,
            @Parameter(description = "账本ID") @RequestParam Long bookId
    ) {
        LambdaQueryWrapper<FinEntries> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinEntries::getId, id).eq(FinEntries::getBookId, bookId);
        boolean result = finEntriesService.remove(wrapper);
        return result ? ResponseResult.success(true) : ResponseResult.error("删除失败或无权限");
    }

    @PutMapping
    @Operation(summary = "修改财务分录")
    public ResponseResult<Boolean> update(
            @RequestBody FinEntries finEntries
    ) {
        // 验证数据是否属于当前账本
        FinEntries existing = finEntriesService.getById(finEntries.getId());
        if (existing == null || !finEntries.getBookId().equals(existing.getBookId())) {
            return ResponseResult.error("修改失败或无权限");
        }
        boolean result = finEntriesService.updateById(finEntries);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询财务分录")
    public ResponseResult<FinEntries> getById(
            @Parameter(description = "分录ID") @PathVariable Long id,
            @Parameter(description = "账本ID") @RequestParam Long bookId
    ) {
        LambdaQueryWrapper<FinEntries> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinEntries::getId, id).eq(FinEntries::getBookId, bookId);
        FinEntries finEntries = finEntriesService.getOne(wrapper);
        return finEntries != null ? ResponseResult.success(finEntries) : ResponseResult.error("查询失败或无权限");
    }

    @GetMapping
    @Operation(summary = "查询所有财务分录列表")
    public ResponseResult<List<FinEntries>> list(
            @Parameter(description = "账本ID") @RequestParam Long bookId
    ) {
        LambdaQueryWrapper<FinEntries> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinEntries::getBookId, bookId);
        List<FinEntries> list = finEntriesService.list(wrapper);
        return ResponseResult.success(list);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询财务分录")
    public ResponseResult<Page<FinEntries>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize,
            @Parameter(description = "账本ID") @RequestParam Long bookId
    ) {
        Page<FinEntries> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<FinEntries> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinEntries::getBookId, bookId);
        Page<FinEntries> resultPage = finEntriesService.page(page, wrapper);
        return ResponseResult.success(resultPage);
    }
}
