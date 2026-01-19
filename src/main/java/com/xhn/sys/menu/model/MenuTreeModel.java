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
    private List<MenuTreeModel> children;

}
