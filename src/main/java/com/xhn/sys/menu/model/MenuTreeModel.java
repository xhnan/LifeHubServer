package com.xhn.sys.menu.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author xhn
 * @date 2025/12/29 15:58
 * @description
 */
@Setter
@Getter
public class MenuTreeModel {

    private Long id;

    private Long parentId;

    private String menuName;

    private Integer level;

    private String icon;

    private Integer sort;

    /** 路由路径 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 菜单类型(目录/菜单/按钮) */
    private Integer menuType;

    /** 是否可见 */
    private Boolean visible;

    /** 权限标识 */
    private String permission;

    private String routerName;

    /** 是否启用 */
    private Boolean status;

    private List<MenuTreeModel> children;
}
