package com.xhn.fin.prices.controller;

import com.xhn.fin.prices.model.FinPrices;
import com.xhn.fin.prices.service.FinPricesService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FinPrices 控制器
 *
 * @author xhn
 * @date 2026-02-04
 */
@RestController
@RequestMapping("/fin/prices")
@Tag(name = "fin", description = "价格管理")
public class FinPricesController {

    @Autowired
    private FinPricesService finPricesService;

    @PostMapping
    @Operation(summary = "新增价格")
    public ResponseResult<Boolean> add(
        @RequestBody FinPrices finPrices
    ) {
        boolean result = finPricesService.save(finPrices);
        return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除价格")
    public ResponseResult<Boolean> delete(
        @Parameter(description = "价格ID") @PathVariable Long id
    ) {
        boolean result = finPricesService.removeById(id);
        return result ? ResponseResult.success(true) : ResponseResult.error("删除失败");
    }

    @PutMapping
    @Operation(summary = "修改价格")
    public ResponseResult<Boolean> update(
        @RequestBody FinPrices finPrices
    ) {
        boolean result = finPricesService.updateById(finPrices);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询价格")
    public ResponseResult<FinPrices> getById(
        @Parameter(description = "价格ID") @PathVariable Long id
    ) {
        FinPrices finPrices = finPricesService.getById(id);
        return finPrices != null ? ResponseResult.success(finPrices) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有价格列表")
    public ResponseResult<List<FinPrices>> list() {
        List<FinPrices> list = finPricesService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询价格")
    public ResponseResult<Page<FinPrices>> page(
        @Parameter(description = "页码") @RequestParam int pageNum,
        @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        Page<FinPrices> page = new Page<>(pageNum, pageSize);
        Page<FinPrices> resultPage = finPricesService.page(page);
        return ResponseResult.success(resultPage);
    }
}