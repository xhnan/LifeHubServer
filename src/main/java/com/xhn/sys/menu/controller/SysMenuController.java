package com.xhn.sys.menu.controller;

import com.xhn.response.ResponseResult;
import com.xhn.sys.menu.model.SysMenu;
import com.xhn.sys.menu.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单控制器
 *
 * @author xhn
 * @date 2025-12-16
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 新增菜单
     *
     * @param sysMenu 菜单信息
     * @return 操作结果
     */
    @PostMapping
    public ResponseResult<SysMenu> create(@RequestBody SysMenu sysMenu) {
        boolean isSaved = sysMenuService.save(sysMenu);
        if (isSaved) {
            return ResponseResult.success(sysMenu);
        }
        return ResponseResult.error("新增菜单失败");
    }

    /**
     * 根据ID删除菜单
     *
     * @param id 菜单ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id) {
        boolean isRemoved = sysMenuService.removeById(id);
        if (isRemoved) {
            return ResponseResult.success();
        }
        return ResponseResult.error("删除菜单失败");
    }

    /**
     * 更新菜单信息
     *
     * @param sysMenu 菜单信息
     * @return 操作结果
     */
    @PutMapping
    public ResponseResult<SysMenu> update(@RequestBody SysMenu sysMenu) {
        boolean isUpdated = sysMenuService.updateById(sysMenu);
        if (isUpdated) {
            return ResponseResult.success(sysMenu);
        }
        return ResponseResult.error("更新菜单失败");
    }

    /**
     * 根据ID查询菜单
     *
     * @param id 菜单ID
     * @return 菜单信息
     */
    @GetMapping("/{id}")
    public ResponseResult<SysMenu> getById(@PathVariable Long id) {
        SysMenu sysMenu = sysMenuService.getById(id);
        if (sysMenu != null) {
            return ResponseResult.success(sysMenu);
        }
        return ResponseResult.error("菜单不存在");
    }

    /**
     * 查询所有菜单
     *
     * @return 菜单列表
     */
    @GetMapping
    public ResponseResult<List<SysMenu>> listAll() {
        List<SysMenu> list = sysMenuService.list();
        return ResponseResult.success(list);
    }
}