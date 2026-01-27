package com.xhn.sys.permission.model;

import com.xhn.sys.role.model.SysRole;
import lombok.Data;

import java.util.List;

/**
 * 用户权限和角色DTO
 *
 * @author xhn
 * @date 2026-01-27
 */
@Data
public class UserPermissionsDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 是否是超级管理员
     */
    private Boolean isSuperAdmin;

    /**
     * 用户角色列表
     */
    private List<SysRole> roles;

    /**
     * 用户权限列表
     */
    private List<SysPermission> permissions;

}
