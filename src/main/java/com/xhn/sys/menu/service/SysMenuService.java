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

    List<MenuTreeModel> getMenuTree();
}