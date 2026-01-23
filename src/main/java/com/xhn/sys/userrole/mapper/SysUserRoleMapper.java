package com.xhn.sys.userrole.mapper;

import com.xhn.sys.role.model.SysRole;
import com.xhn.sys.userrole.model.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 用户角色关联表 Mapper 接口
 * @author xhn
 * @date 2025-12-21 14:18:02
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {


    List<SysRole> getRolesByUserId(Long userId);
}