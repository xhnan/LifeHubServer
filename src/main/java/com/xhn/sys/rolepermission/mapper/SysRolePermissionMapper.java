package com.xhn.sys.rolepermission.mapper;

import com.xhn.sys.permission.model.SysPermission;
import com.xhn.sys.rolepermission.model.SysRolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author xhn
 * @date 2025-12-20 16:14:30
 */
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<SysPermission> selectPermissionsByRoleId(Long roleId);
}