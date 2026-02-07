package com.xhn.fin.accounts.controller;

import com.xhn.base.utils.SecurityUtils;
import com.xhn.fin.accounts.model.FinAccounts;
import com.xhn.fin.accounts.model.SubjectTreeDTO;
import com.xhn.fin.accounts.service.FinAccountsService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * FinAccounts 控制器
 *
 * @author xhn
 * @date 2026-02-04
 */
@RestController
@RequestMapping("/fin/accounts")
@Tag(name = "fin", description = "账户管理")
public class FinAccountsController {

    @Autowired
    private FinAccountsService finAccountsService;

    @PostMapping
    @Operation(summary = "新增账户")
    public Mono<ResponseResult<Boolean>> add(
            @RequestBody FinAccounts finAccounts
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    finAccounts.setUserId(userId);
                    try {
                        boolean result = finAccountsService.saveAccount(finAccounts);
                        return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
                    } catch (RuntimeException e) {
                        return ResponseResult.error(e.getMessage());
                    }
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除账户")
    public Mono<ResponseResult<Boolean>> delete(
            @Parameter(description = "账户ID") @PathVariable Long id
    ) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    boolean result = finAccountsService.removeByIdAndUserId(id, userId);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.error("删除失败或无权限"));
                });
    }

    @PutMapping
    @Operation(summary = "修改账户")
    public Mono<ResponseResult<Boolean>> update(
            @RequestBody FinAccounts finAccounts
    ) {
        return SecurityUtils.getCurrentUserId()
                .flatMap(userId -> {
                    finAccounts.setUserId(userId);
                    boolean result = finAccountsService.updateAccountAndUserId(finAccounts, userId);
                    return Mono.just(result ? ResponseResult.success(true) : ResponseResult.error("修改失败或无权限"));
                });
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询账户")
    public Mono<ResponseResult<FinAccounts>> getById(
            @Parameter(description = "账户ID") @PathVariable Long id
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    FinAccounts finAccounts = finAccountsService.getByIdAndUserId(id, userId);
                    return finAccounts != null ? ResponseResult.success(finAccounts) : ResponseResult.error("查询失败或无权限");
                });
    }

    @GetMapping
    @Operation(summary = "查询所有账户列表")
    public Mono<ResponseResult<List<FinAccounts>>> list() {
        return SecurityUtils.getCurrentUserId()
                .map(finAccountsService::listByUserId)
                .map(ResponseResult::success);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询账户")
    public Mono<ResponseResult<Page<FinAccounts>>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> {
                    Page<FinAccounts> page = new Page<>(pageNum, pageSize);
                    return finAccountsService.pageByUserId(page, userId);
                })
                .map(ResponseResult::success);
    }

    // ========== 科目表相关接口 ==========

    @GetMapping("/subjects/tree")
    @Operation(summary = "获取科目树形结构")
    public Mono<ResponseResult<List<SubjectTreeDTO>>> getSubjectTree(
            @Parameter(description = "账户类型：ASSET(资产)、LIABILITY(负债)、EQUITY(权益)、INCOME(收入)、EXPENSE(支出)，不传则查询全部")
            @RequestParam(required = false) String accountType
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> finAccountsService.getSubjectTreeByUserId(accountType, userId))
                .map(ResponseResult::success);
    }

    @GetMapping("/subjects")
    @Operation(summary = "根据父级ID获取子科目列表")
    public Mono<ResponseResult<List<SubjectTreeDTO>>> getSubjectsByParentId(
            @Parameter(description = "父级ID，不传或传0查询根节点") @RequestParam(required = false) Long parentId
    ) {
        return SecurityUtils.getCurrentUserId()
                .map(userId -> finAccountsService.getSubjectsByParentIdAndUserId(parentId, userId))
                .map(ResponseResult::success);
    }
}