package com.xhn.sys.userrole.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.sys.role.model.SysRole;
import com.xhn.sys.userrole.model.SysUserRole;

import java.util.List;

/**
 * 用户角色关联表 服务类
 *
 * @author xhn
 * @date 2025-12-19
 */
public interface SysUserRoleService extends IService<SysUserRole> {

    List<SysRole> getRolesByUserId(Long userId);
}