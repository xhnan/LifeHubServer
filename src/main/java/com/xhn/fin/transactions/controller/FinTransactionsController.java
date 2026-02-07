package com.xhn.fin.transactions.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.base.utils.SecurityUtils;
import com.xhn.fin.transactions.model.FinTransactions;
import com.xhn.fin.transactions.service.FinTransactionsService;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
    public Mono<ResponseResult<Boolean>> add(
            @RequestBody FinTransactions finTransactions
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    finTransactions.setUserId(userId);
                    boolean result = finTransactionsService.save(finTransactions);
                    return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除财务交易记录")
    public Mono<ResponseResult<Boolean>> delete(
            @Parameter(description = "财务交易记录ID") @PathVariable Long id
    ) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    LambdaQueryWrapper<FinTransactions> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(FinTransactions::getId, id).eq(FinTransactions::getUserId, userId);
                    boolean result = finTransactionsService.remove(wrapper);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.error("删除失败或无权限"));
                });
    }

    @PutMapping
    @Operation(summary = "修改财务交易记录")
    public Mono<ResponseResult<Boolean>> update(
            @RequestBody FinTransactions finTransactions
    ) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    // 验证数据是否属于当前用户
                    FinTransactions existing = finTransactionsService.getById(finTransactions.getId());
                    if (existing == null || !userId.equals(existing.getUserId())) {
                        return Mono.just(ResponseResult.error("修改失败或无权限"));
                    }
                    finTransactions.setUserId(userId);
                    boolean result = finTransactionsService.updateById(finTransactions);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.error("修改失败"));
                });
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询财务交易记录")
    public Mono<ResponseResult<FinTransactions>> getById(
            @Parameter(description = "财务交易记录ID") @PathVariable Long id
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    LambdaQueryWrapper<FinTransactions> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(FinTransactions::getId, id).eq(FinTransactions::getUserId, userId);
                    FinTransactions finTransactions = finTransactionsService.getOne(wrapper);
                    return finTransactions != null ? ResponseResult.success(finTransactions) : ResponseResult.error("查询失败或无权限");
                });
    }

    @GetMapping
    @Operation(summary = "查询所有财务交易记录列表")
    public Mono<ResponseResult<List<FinTransactions>>> list() {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    LambdaQueryWrapper<FinTransactions> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(FinTransactions::getUserId, userId);
                    List<FinTransactions> list = finTransactionsService.list(wrapper);
                    return ResponseResult.success(list);
                });
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询财务交易记录")
    public Mono<ResponseResult<Page<FinTransactions>>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize,
            @Parameter(description = "开始日期 (yyyy-MM-dd)") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期 (yyyy-MM-dd)") @RequestParam(required = false) String endDate
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    Page<FinTransactions> page = new Page<>(pageNum, pageSize);
                    Page<FinTransactions> resultPage = finTransactionsService.pageByUserIdAndDateRange(page, userId, startDate, endDate);
                    return ResponseResult.success(resultPage);
                });
    }
}
