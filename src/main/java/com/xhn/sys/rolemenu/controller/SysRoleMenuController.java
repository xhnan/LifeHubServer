package com.xhn.sys.rolemenu.controller;

import com.xhn.response.ResponseResult;
import com.xhn.sys.rolemenu.model.SysRoleMenu;
import com.xhn.sys.rolemenu.service.SysRoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色菜单关联控制器
 * 
 * @author xhn
 * @date 2025-12-19
 */
@RestController
@RequestMapping("/sys/rolemenu")
public class SysRoleMenuController {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    /**
     * 新增角色菜单关联
     * 
     * @param sysRoleMenu 角色菜单关联实体
     * @return 操作结果
     */
    @PostMapping
    public ResponseResult<SysRoleMenu> add(@RequestBody SysRoleMenu sysRoleMenu) {
        boolean result = sysRoleMenuService.save(sysRoleMenu);
        if (result) {
            return ResponseResult.success(sysRoleMenu);
        }
        return ResponseResult.error("新增失败");
    }

    /**
     * 根据ID删除角色菜单关联
     * 
     * @param id 主键ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id) {
        boolean result = sysRoleMenuService.removeById(id);
        if (result) {
            return ResponseResult.success();
        }
        return ResponseResult.error("删除失败");
    }

    /**
     * 修改角色菜单关联
     * 
     * @param sysRoleMenu 角色菜单关联实体
     * @return 操作结果
     */
    @PutMapping
    public ResponseResult<SysRoleMenu> update(@RequestBody SysRoleMenu sysRoleMenu) {
        boolean result = sysRoleMenuService.updateById(sysRoleMenu);
        if (result) {
            return ResponseResult.success(sysRoleMenu);
        }
        return ResponseResult.error("修改失败");
    }

    /**
     * 根据ID查询角色菜单关联
     * 
     * @param id 主键ID
     * @return 查询结果
     */
    @GetMapping("/{id}")
    public ResponseResult<SysRoleMenu> getById(@PathVariable Long id) {
        SysRoleMenu sysRoleMenu = sysRoleMenuService.getById(id);
        if (sysRoleMenu != null) {
            return ResponseResult.success(sysRoleMenu);
        }
        return ResponseResult.error("查询失败");
    }

    /**
     * 查询所有角色菜单关联
     * 
     * @return 查询结果
     */
    @GetMapping
    public ResponseResult<List<SysRoleMenu>> listAll() {
        List<SysRoleMenu> list = sysRoleMenuService.list();
        return ResponseResult.success(list);
    }
}