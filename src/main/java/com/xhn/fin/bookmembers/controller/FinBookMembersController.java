package com.xhn.fin.bookmembers.controller;

import com.xhn.fin.bookmembers.model.FinBookMembers;
import com.xhn.fin.bookmembers.service.FinBookMembersService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FinBookMembers 控制器
 *
 * @author xhn
 * @date 2026-02-07
 */
@RestController
@RequestMapping("/fin/bookmembers")
@Tag(name = "fin", description = "账本成员管理")
public class FinBookMembersController {

    @Autowired
    private FinBookMembersService finBookMembersService;

    @PostMapping
    @Operation(summary = "新增账本成员")
    public ResponseResult<Boolean> add(
            @RequestBody FinBookMembers finBookMembers
    ) {
        boolean result = finBookMembersService.save(finBookMembers);
        return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除账本成员")
    public ResponseResult<Boolean> delete(
            @Parameter(description = "账本成员ID") @PathVariable Long id
    ) {
        boolean result = finBookMembersService.removeById(id);
        return result ? ResponseResult.success(true) : ResponseResult.error("删除失败");
    }

    @PutMapping
    @Operation(summary = "修改账本成员")
    public ResponseResult<Boolean> update(
            @RequestBody FinBookMembers finBookMembers
    ) {
        boolean result = finBookMembersService.updateById(finBookMembers);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询账本成员")
    public ResponseResult<FinBookMembers> getById(
            @Parameter(description = "账本成员ID") @PathVariable Long id
    ) {
        FinBookMembers finBookMembers = finBookMembersService.getById(id);
        return finBookMembers != null ? ResponseResult.success(finBookMembers) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有账本成员列表")
    public ResponseResult<List<FinBookMembers>> list() {
        List<FinBookMembers> list = finBookMembersService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询账本成员")
    public ResponseResult<Page<FinBookMembers>> page(
            @Parameter(description = "页码") @RequestParam int pageNum,
            @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        Page<FinBookMembers> page = new Page<>(pageNum, pageSize);
        Page<FinBookMembers> resultPage = finBookMembersService.page(page);
        return ResponseResult.success(resultPage);
    }
}