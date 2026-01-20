package com.xhn.sys.rolemenu.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色菜单关联表实体类
 * 
 * @author xhn
 * @date 2025-12-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_role_menu")
public class BaseSysRoleMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID，对应 sys_role.id
     */
    private Long roleId;

    /**
     * 菜单ID，对应 sys_menu.id
     */
    private Long menuId;

    /**
     * 权限授权时间
     */
    private LocalDateTime createTime;

}