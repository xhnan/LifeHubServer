package com.xhn.sys.permission.mapper;

import com.xhn.sys.permission.model.SysPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author xhn
 * @date 2025-12-19
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 根据用户ID查询该用户角色可访问的权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<SysPermission> selectPermissionsByUserId(Long userId);
}