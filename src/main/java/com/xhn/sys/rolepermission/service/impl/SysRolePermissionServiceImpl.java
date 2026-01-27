package com.xhn.sys.rolepermission.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhn.sys.permission.model.SysPermission;
import com.xhn.sys.rolepermission.mapper.SysRolePermissionMapper;
import com.xhn.sys.rolepermission.model.SysRolePermission;
import com.xhn.sys.rolepermission.service.SysRolePermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色权限关联表 Service实现类
 *
 * @author xhn
 * @date 2025-12-19
 */
@Service
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> implements SysRolePermissionService {

    @Override
    public List<SysPermission> getPermissionsByRoleId(Long roleId) {
        return baseMapper.selectPermissionsByRoleId(roleId);
    }
}