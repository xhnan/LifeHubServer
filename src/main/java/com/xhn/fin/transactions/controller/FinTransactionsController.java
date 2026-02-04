package com.xhn.fin.transactions.controller;

import com.xhn.fin.transactions.model.FinTransactions;
import com.xhn.fin.transactions.service.FinTransactionsService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 财务交易记录 控制器
 *
 * @author xhn
 * @date 2026-02-04
 */
@RestController
@RequestMapping("/fin/transactions")
@Tag(name = "fin", description = "财务交易记录管理")
public class FinTransactionsController {

    @Autowired
    private FinTransactionsService finTransactionsService;

    @PostMapping
    @Operation(summary = "新增财务交易记录")
    public ResponseResult<Boolean> add(
            @RequestBody FinTransactions finTransactions
    ) {
        boolean result = finTransactionsService.save(finTransactions);
        return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除财务交易记录")
    public ResponseResult<Boolean> delete(
            @Parameter(description = "财务交易记录ID") @PathVariable Long id
    ) {
        boolean result = finTransactionsService.removeById(id);
        return result ? ResponseResult.success(true) : ResponseResult.error("删除失败");
    }

    @PutMapping
    @Operation(summary = "修改财务交易记录")
    public ResponseResult<Boolean> update(
            @RequestBody FinTransactions finTransactions
    ) {
        boolean result = finTransactionsService.updateById(finTransactions);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询财务交易记录")
    public ResponseResult<FinTransactions> getById(
            @Parameter(description = "财务交易记录ID") @PathVariable Long id
    ) {
        FinTransactions finTransactions = finTransactionsService.getById(id);
        return finTransactions != null ? ResponseResult.success(finTransactions) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有财务交易记录列表")
    public ResponseResult<List<FinTransactions>> list() {
        List<FinTransactions> list = finTransactionsService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询财务交易记录")
    public ResponseResult<Page<FinTransactions>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        Page<FinTransactions> page = new Page<>(pageNum, pageSize);
        Page<FinTransactions> resultPage = finTransactionsService.page(page);
        return ResponseResult.success(resultPage);
    }
}