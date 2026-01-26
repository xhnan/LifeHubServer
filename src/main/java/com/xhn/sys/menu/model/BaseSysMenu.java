package com.xhn.sys.menu.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 菜单表实体类
 * 
 * @author xhn
 * @date 2025-12-27 10:57:26
 */
@Data
@TableName("sys_menu")
public class BaseSysMenu {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 父菜单ID，0表示根菜单
     */
    private Long parentId;

    /**
     * 菜单名称（前端显示）
     */
    private String menuName;

    /**
     * 菜单权限标识（按钮或接口权限）
     */
    private String menuCode;

    /**
     * 菜单类型：1目录 2菜单 3按钮
     */
    private Integer menuType;

    /**
     * 前端路由路径
     */
    private String path;

    /**
     * 前端组件路径
     */
    private String component;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 菜单排序，数字越小越靠前
     */
    private Integer sortOrder;

    /**
     * 是否在菜单中显示
     */
    private Boolean visible;

    /**
     * 是否启用
     */
    private Boolean status;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 创建人
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新人
     */
    private Long updatedBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 路由名称
     */
    private String routerName;

}