package com.xhn.sys.rolepermission.controller;

import com.xhn.response.ResponseResult;
import com.xhn.sys.rolepermission.model.SysRolePermission;
import com.xhn.sys.rolepermission.service.SysRolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色权限关联控制器
 * 
 * @author xhn
 * @date 2025-01-19
 */
@RestController
@RequestMapping("/sys/rolepermission")
public class SysRolePermissionController {

    @Autowired
    private SysRolePermissionService sysRolePermissionService;

    /**
     * 新增角色权限关联
     * 
     * @param sysRolePermission 角色权限关联信息
     * @return 操作结果
     */
    @PostMapping
    public ResponseResult<Boolean> add(@RequestBody SysRolePermission sysRolePermission) {
        boolean result = sysRolePermissionService.save(sysRolePermission);
        return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
    }

    /**
     * 根据ID删除角色权限关联
     * 
     * @param id 主键ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Boolean> delete(@PathVariable Long id) {
        boolean result = sysRolePermissionService.removeById(id);
        return result ? ResponseResult.success(true) : ResponseResult.error("删除失败");
    }

    /**
     * 修改角色权限关联
     * 
     * @param sysRolePermission 角色权限关联信息
     * @return 操作结果
     */
    @PutMapping
    public ResponseResult<Boolean> update(@RequestBody SysRolePermission sysRolePermission) {
        boolean result = sysRolePermissionService.updateById(sysRolePermission);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    /**
     * 根据ID查询角色权限关联
     * 
     * @param id 主键ID
     * @return 角色权限关联信息
     */
    @GetMapping("/{id}")
    public ResponseResult<SysRolePermission> getById(@PathVariable Long id) {
        SysRolePermission sysRolePermission = sysRolePermissionService.getById(id);
        return sysRolePermission != null ? ResponseResult.success(sysRolePermission) : ResponseResult.error("查询失败");
    }

    /**
     * 查询所有角色权限关联
     * 
     * @return 角色权限关联列表
     */
    @GetMapping
    public ResponseResult<List<SysRolePermission>> getAll() {
        List<SysRolePermission> list = sysRolePermissionService.list();
        return ResponseResult.success(list);
    }
}