package com.xhn.sys.menu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.sys.menu.model.MenuTreeModel;
import com.xhn.sys.menu.model.SysMenu;

import java.util.List;

/**
 * 菜单表 服务类
 *
 * @author xhn
 * @date 2025-12-17
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 获取菜单树（所有菜单）
     * @return 菜单树
     */
    List<MenuTreeModel> getMenuTree();

    /**
     * 根据用户ID获取菜单树
     * 超级管理员返回所有菜单，其他用户根据角色权限返回菜单
     * @param userId 用户ID
     * @return 菜单树
     */
    List<MenuTreeModel> getMenuTreeByUserId(Long userId);
}