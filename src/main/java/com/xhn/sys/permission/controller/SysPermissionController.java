package com.xhn.sys.permission.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.response.ResponseResult;
import com.xhn.sys.permission.model.SysPermission;
import com.xhn.sys.permission.service.SysPermissionService;
import com.xhn.sys.role.model.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限管理控制器
 * 
 * @author xhn
 * @date 2025-12-19
 */
@RestController
@RequestMapping("/sys/permission")
public class SysPermissionController {

    @Autowired
    private SysPermissionService sysPermissionService;

    /**
     * 新增权限
     * 
     * @param sysPermission 权限信息
     * @return 操作结果
     */
    @PostMapping
    public ResponseResult<SysPermission> create(@RequestBody SysPermission sysPermission) {
        boolean result = sysPermissionService.save(sysPermission);
        if (result) {
            return ResponseResult.success(sysPermission);
        }
        return ResponseResult.error("新增权限失败");
    }

    /**
     * 根据ID删除权限
     * 
     * @param id 权限ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id) {
        boolean result = sysPermissionService.removeById(id);
        if (result) {
            return ResponseResult.success();
        }
        return ResponseResult.error("删除权限失败");
    }

    /**
     * 更新权限信息
     * 
     * @param sysPermission 权限信息
     * @return 操作结果
     */
    @PutMapping
    public ResponseResult<SysPermission> update(@RequestBody SysPermission sysPermission) {
        boolean result = sysPermissionService.updateById(sysPermission);
        if (result) {
            return ResponseResult.success(sysPermission);
        }
        return ResponseResult.error("更新权限失败");
    }

    /**
     * 根据ID查询权限
     * 
     * @param id 权限ID
     * @return 权限信息
     */
    @GetMapping("/{id}")
    public ResponseResult<SysPermission> getById(@PathVariable Long id) {
        SysPermission sysPermission = sysPermissionService.getById(id);
        if (sysPermission != null) {
            return ResponseResult.success(sysPermission);
        }
        return ResponseResult.error("权限不存在");
    }

    /**
     * 查询所有权限列表
     * 
     * @return 权限列表
     */
    @GetMapping
    public ResponseResult<List<SysPermission>> listAll() {
        List<SysPermission> list = sysPermissionService.list();
        return ResponseResult.success(list);
    }


    @GetMapping("/page")
    public ResponseResult<Page<SysPermission>> pageAll(@RequestParam int pageNum, @RequestParam int pageSize) {
        Page<SysPermission> page = new Page<>(pageNum, pageSize);
        Page<SysPermission> resultPage = sysPermissionService.page(page);
        return ResponseResult.success(resultPage);
    }
    //app-codes
    @GetMapping("/appCodes")
    public ResponseResult<List<String>> getAppCodes() {
        List<String> appCodes = new ArrayList<>();
        appCodes.add("SYS");
        return ResponseResult.success(appCodes);
    }


}