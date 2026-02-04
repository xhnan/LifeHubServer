package com.xhn.fin.budgets.controller;

import com.xhn.fin.budgets.model.FinBudgets;
import com.xhn.fin.budgets.service.FinBudgetsService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预算信息控制器
 *
 * @author xhn
 * @date 2026-02-04
 */
@RestController
@RequestMapping("/fin/budgets")
@Tag(name = "预算管理", description = "预算信息管理")
public class FinBudgetsController {

    @Autowired
    private FinBudgetsService finBudgetsService;

    @PostMapping
    @Operation(summary = "新增预算")
    public ResponseResult<Boolean> add(
        @RequestBody FinBudgets finBudgets
    ) {
        boolean result = finBudgetsService.save(finBudgets);
        return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除预算")
    public ResponseResult<Boolean> delete(
        @Parameter(description = "预算ID") @PathVariable Long id
    ) {
        boolean result = finBudgetsService.removeById(id);
        return result ? ResponseResult.success(true) : ResponseResult.error("删除失败");
    }

    @PutMapping
    @Operation(summary = "修改预算")
    public ResponseResult<Boolean> update(
        @RequestBody FinBudgets finBudgets
    ) {
        boolean result = finBudgetsService.updateById(finBudgets);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询预算")
    public ResponseResult<FinBudgets> getById(
        @Parameter(description = "预算ID") @PathVariable Long id
    ) {
        FinBudgets finBudgets = finBudgetsService.getById(id);
        return finBudgets != null ? ResponseResult.success(finBudgets) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有预算列表")
    public ResponseResult<List<FinBudgets>> list() {
        List<FinBudgets> list = finBudgetsService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询预算")
    public ResponseResult<Page<FinBudgets>> page(
        @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
        @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize
    ) {
        Page<FinBudgets> page = new Page<>(pageNum, pageSize);
        Page<FinBudgets> resultPage = finBudgetsService.page(page);
        return ResponseResult.success(resultPage);
    }
}