package com.xhn.fin.accounts.controller;

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
    public ResponseResult<Boolean> add(
            @RequestBody FinAccounts finAccounts
    ) {
        try {
            boolean result = finAccountsService.saveAccount(finAccounts);
            return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
        } catch (RuntimeException e) {
            return ResponseResult.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除账户")
    public ResponseResult<Boolean> delete(
            @Parameter(description = "账户ID") @PathVariable Long id,
            @Parameter(description = "账本ID") @RequestParam Long bookId
    ) {
        boolean result = finAccountsService.removeByIdAndBookId(id, bookId);
        return result ? ResponseResult.success(true) : ResponseResult.error("删除失败或无权限");
    }

    @PutMapping
    @Operation(summary = "修改账户")
    public ResponseResult<Boolean> update(
            @RequestBody FinAccounts finAccounts
    ) {
        boolean result = finAccountsService.updateAccountAndBookId(finAccounts, finAccounts.getBookId());
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败或无权限");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询账户")
    public ResponseResult<FinAccounts> getById(
            @Parameter(description = "账户ID") @PathVariable Long id,
            @Parameter(description = "账本ID") @RequestParam Long bookId
    ) {
        FinAccounts finAccounts = finAccountsService.getByIdAndBookId(id, bookId);
        return finAccounts != null ? ResponseResult.success(finAccounts) : ResponseResult.error("查询失败或无权限");
    }

    @GetMapping
    @Operation(summary = "查询所有账户列表")
    public ResponseResult<List<FinAccounts>> list(
            @Parameter(description = "账本ID") @RequestParam Long bookId
    ) {
        List<FinAccounts> list = finAccountsService.listByBookId(bookId);
        return ResponseResult.success(list);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询账户")
    public ResponseResult<Page<FinAccounts>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize,
            @Parameter(description = "账本ID") @RequestParam Long bookId
    ) {
        Page<FinAccounts> page = new Page<>(pageNum, pageSize);
        Page<FinAccounts> resultPage = finAccountsService.pageByBookId(page, bookId);
        return ResponseResult.success(resultPage);
    }

    // ========== 科目表相关接口 ==========

    @PostMapping("/init")
    @Operation(summary = "初始化账本默认科目")
    public ResponseResult<Boolean> initDefaultAccounts(
            @Parameter(description = "账本ID") @RequestParam Long bookId
    ) {
        boolean result = finAccountsService.initDefaultAccounts(bookId);
        return result ? ResponseResult.success(true) : ResponseResult.error("初始化失败");
    }

    @GetMapping("/subjects/tree")
    @Operation(summary = "获取科目树形结构")
    public ResponseResult<List<SubjectTreeDTO>> getSubjectTree(
            @Parameter(description = "账户类型：ASSET(资产)、LIABILITY(负债)、EQUITY(权益)、INCOME(收入)、EXPENSE(支出)，不传则查询全部")
            @RequestParam(required = false) String accountType,
            @Parameter(description = "账本ID") @RequestParam Long bookId
    ) {
        List<SubjectTreeDTO> tree = finAccountsService.getSubjectTreeByBookId(accountType, bookId);
        return ResponseResult.success(tree);
    }

    @GetMapping("/subjects")
    @Operation(summary = "根据父级ID获取子科目列表")
    public ResponseResult<List<SubjectTreeDTO>> getSubjectsByParentId(
            @Parameter(description = "父级ID，不传或传0查询根节点") @RequestParam(required = false) Long parentId,
            @Parameter(description = "账本ID") @RequestParam Long bookId
    ) {
        List<SubjectTreeDTO> subjects = finAccountsService.getSubjectsByParentIdAndBookId(parentId, bookId);
        return ResponseResult.success(subjects);
    }
}
