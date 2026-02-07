package com.xhn.fin.transactions.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.fin.transactions.model.FinTransactions;
import com.xhn.fin.transactions.service.FinTransactionsService;
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
            @Parameter(description = "财务交易记录ID") @PathVariable Long id,
            @Parameter(description = "账本ID") @RequestParam Long bookId
    ) {
        LambdaQueryWrapper<FinTransactions> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinTransactions::getId, id).eq(FinTransactions::getBookId, bookId);
        boolean result = finTransactionsService.remove(wrapper);
        return result ? ResponseResult.success(true) : ResponseResult.error("删除失败或无权限");
    }

    @PutMapping
    @Operation(summary = "修改财务交易记录")
    public ResponseResult<Boolean> update(
            @RequestBody FinTransactions finTransactions
    ) {
        // 验证数据是否属于当前账本
        FinTransactions existing = finTransactionsService.getById(finTransactions.getId());
        if (existing == null || !finTransactions.getBookId().equals(existing.getBookId())) {
            return ResponseResult.error("修改失败或无权限");
        }
        boolean result = finTransactionsService.updateById(finTransactions);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询财务交易记录")
    public ResponseResult<FinTransactions> getById(
            @Parameter(description = "财务交易记录ID") @PathVariable Long id,
            @Parameter(description = "账本ID") @RequestParam Long bookId
    ) {
        LambdaQueryWrapper<FinTransactions> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinTransactions::getId, id).eq(FinTransactions::getBookId, bookId);
        FinTransactions finTransactions = finTransactionsService.getOne(wrapper);
        return finTransactions != null ? ResponseResult.success(finTransactions) : ResponseResult.error("查询失败或无权限");
    }

    @GetMapping
    @Operation(summary = "查询所有财务交易记录列表")
    public ResponseResult<List<FinTransactions>> list(
            @Parameter(description = "账本ID") @RequestParam Long bookId
    ) {
        LambdaQueryWrapper<FinTransactions> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinTransactions::getBookId, bookId);
        List<FinTransactions> list = finTransactionsService.list(wrapper);
        return ResponseResult.success(list);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询财务交易记录")
    public ResponseResult<Page<FinTransactions>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize,
            @Parameter(description = "账本ID") @RequestParam Long bookId,
            @Parameter(description = "开始日期 (yyyy-MM-dd)") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期 (yyyy-MM-dd)") @RequestParam(required = false) String endDate
    ) {
        Page<FinTransactions> page = new Page<>(pageNum, pageSize);
        Page<FinTransactions> resultPage = finTransactionsService.pageByBookIdAndDateRange(page, bookId, startDate, endDate);
        return ResponseResult.success(resultPage);
    }
}
