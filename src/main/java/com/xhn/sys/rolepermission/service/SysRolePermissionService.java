package com.xhn.sys.rolepermission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.sys.permission.model.SysPermission;
import com.xhn.sys.rolepermission.model.SysRolePermission;

import java.util.List;

/**
 * 角色权限关联表 服务类
 *
 * @author xhn
 * @date 2025-12-25 10:14:41
 */
public interface SysRolePermissionService extends IService<SysRolePermission> {

    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<SysPermission> getPermissionsByRoleId(Long roleId);
}