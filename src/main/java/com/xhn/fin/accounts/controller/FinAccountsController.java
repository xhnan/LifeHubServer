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
        boolean result = finAccountsService.save(finAccounts);
        return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除账户")
    public ResponseResult<Boolean> delete(
            @Parameter(description = "账户ID") @PathVariable Long id
    ) {
        boolean result = finAccountsService.removeById(id);
        return result ? ResponseResult.success(true) : ResponseResult.error("删除失败");
    }

    @PutMapping
    @Operation(summary = "修改账户")
    public ResponseResult<Boolean> update(
            @RequestBody FinAccounts finAccounts
    ) {
        boolean result = finAccountsService.updateById(finAccounts);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询账户")
    public ResponseResult<FinAccounts> getById(
            @Parameter(description = "账户ID") @PathVariable Long id
    ) {
        FinAccounts finAccounts = finAccountsService.getById(id);
        return finAccounts != null ? ResponseResult.success(finAccounts) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有账户列表")
    public ResponseResult<List<FinAccounts>> list() {
        List<FinAccounts> list = finAccountsService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询账户")
    public ResponseResult<Page<FinAccounts>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        Page<FinAccounts> page = new Page<>(pageNum, pageSize);
        Page<FinAccounts> resultPage = finAccountsService.page(page);
        return ResponseResult.success(resultPage);
    }

    // ========== 科目表相关接口 ==========

    @GetMapping("/subjects/tree")
    @Operation(summary = "获取科目树形结构")
    public ResponseResult<List<SubjectTreeDTO>> getSubjectTree() {
        List<SubjectTreeDTO> tree = finAccountsService.getSubjectTree();
        return ResponseResult.success(tree);
    }

    @GetMapping("/subjects")
    @Operation(summary = "根据父级ID获取子科目列表")
    public ResponseResult<List<SubjectTreeDTO>> getSubjectsByParentId(
            @Parameter(description = "父级ID，不传或传0查询根节点") @RequestParam(required = false) Long parentId
    ) {
        List<SubjectTreeDTO> subjects = finAccountsService.getSubjectsByParentId(parentId);
        return ResponseResult.success(subjects);
    }
}