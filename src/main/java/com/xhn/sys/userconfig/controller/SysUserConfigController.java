package com.xhn.sys.userconfig.controller;

import com.xhn.sys.userconfig.model.SysUserConfig;
import com.xhn.sys.userconfig.service.SysUserConfigService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户配置控制器
 *
 * @author xhn
 * @date 2026-02-12
 */
@RestController
@RequestMapping("/sys/userconfig")
@Tag(name = "用户配置", description = "用户配置管理")
public class SysUserConfigController {

    @Autowired
    private SysUserConfigService sysUserConfigService;

    @PostMapping
    @Operation(summary = "新增用户配置")
    public ResponseResult<Boolean> add(
        @RequestBody SysUserConfig sysUserConfig
    ) {
        boolean result = sysUserConfigService.save(sysUserConfig);
        return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户配置")
    public ResponseResult<Boolean> delete(
        @Parameter(description = "配置ID") @PathVariable Long id
    ) {
        boolean result = sysUserConfigService.removeById(id);
        return result ? ResponseResult.success(true) : ResponseResult.error("删除失败");
    }

    @PutMapping
    @Operation(summary = "修改用户配置")
    public ResponseResult<Boolean> update(
        @RequestBody SysUserConfig sysUserConfig
    ) {
        boolean result = sysUserConfigService.updateById(sysUserConfig);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询用户配置")
    public ResponseResult<SysUserConfig> getById(
        @Parameter(description = "配置ID") @PathVariable Long id
    ) {
        SysUserConfig sysUserConfig = sysUserConfigService.getById(id);
        return sysUserConfig != null ? ResponseResult.success(sysUserConfig) : ResponseResult.error("查询失败");
    }

    @GetMapping
    @Operation(summary = "查询所有用户配置列表")
    public ResponseResult<List<SysUserConfig>> list() {
        List<SysUserConfig> list = sysUserConfigService.list();
        return ResponseResult.success(list);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询用户配置")
    public ResponseResult<Page<SysUserConfig>> page(
        @Parameter(description = "页码") @RequestParam int pageNum,
        @Parameter(description = "每页数量") @RequestParam int pageSize
    ) {
        Page<SysUserConfig> page = new Page<>(pageNum, pageSize);
        Page<SysUserConfig> resultPage = sysUserConfigService.page(page);
        return ResponseResult.success(resultPage);
    }
}