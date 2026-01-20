package com.xhn.sys.permissionmenu.controller;

import com.xhn.response.ResponseResult;
import com.xhn.sys.permissionmenu.model.SysPermissionMenu;
import com.xhn.sys.permissionmenu.service.SysPermissionMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限菜单控制器
 * 
 * @author xhn
 * @date 2025-01-19
 */
@RestController
@RequestMapping("/sys/permissionmenu")
public class SysPermissionMenuController {

    @Autowired
    private SysPermissionMenuService sysPermissionMenuService;

    /**
     * 新增权限菜单
     * 
     * @param sysPermissionMenu 权限菜单实体
     * @return 操作结果
     */
    @PostMapping
    public ResponseResult<SysPermissionMenu> add(@RequestBody SysPermissionMenu sysPermissionMenu) {
        boolean result = sysPermissionMenuService.save(sysPermissionMenu);
        if (result) {
            return ResponseResult.success(sysPermissionMenu);
        }
        return ResponseResult.error("新增权限菜单失败");
    }

    /**
     * 根据ID删除权限菜单
     * 
     * @param id 权限菜单ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id) {
        boolean result = sysPermissionMenuService.removeById(id);
        if (result) {
            return ResponseResult.success();
        }
        return ResponseResult.error("删除权限菜单失败");
    }

    /**
     * 修改权限菜单
     * 
     * @param sysPermissionMenu 权限菜单实体
     * @return 操作结果
     */
    @PutMapping
    public ResponseResult<SysPermissionMenu> update(@RequestBody SysPermissionMenu sysPermissionMenu) {
        boolean result = sysPermissionMenuService.updateById(sysPermissionMenu);
        if (result) {
            return ResponseResult.success(sysPermissionMenu);
        }
        return ResponseResult.error("修改权限菜单失败");
    }

    /**
     * 根据ID查询权限菜单
     * 
     * @param id 权限菜单ID
     * @return 权限菜单信息
     */
    @GetMapping("/{id}")
    public ResponseResult<SysPermissionMenu> getById(@PathVariable Long id) {
        SysPermissionMenu sysPermissionMenu = sysPermissionMenuService.getById(id);
        if (sysPermissionMenu != null) {
            return ResponseResult.success(sysPermissionMenu);
        }
        return ResponseResult.error("未找到对应的权限菜单");
    }

    /**
     * 查询所有权限菜单列表
     * 
     * @return 权限菜单列表
     */
    @GetMapping
    public ResponseResult<List<SysPermissionMenu>> list() {
        List<SysPermissionMenu> list = sysPermissionMenuService.list();
        return ResponseResult.success(list);
    }
}