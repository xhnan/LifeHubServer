package com.xhn.sys.permissionmenu.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限菜单关联表实体类
 * 
 * @author xhn
 * @date 2025-01-22
 */
@Data
@TableName("sys_permission_menu")
public class BaseSysPermissionMenu {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 权限ID
     */
    private Long permissionId;

    /**
     * 菜单ID
     */
    private Long menuId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

}