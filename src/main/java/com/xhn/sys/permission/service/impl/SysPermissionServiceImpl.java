package com.xhn.sys.permission.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhn.base.constants.SecurityConstants;
import com.xhn.base.utils.SecurityUtils;
import com.xhn.sys.permission.mapper.SysPermissionMapper;
import com.xhn.sys.permission.model.SysPermission;
import com.xhn.sys.permission.model.UserPermissionsDTO;
import com.xhn.sys.permission.service.SysPermissionService;
import com.xhn.sys.role.model.SysRole;
import com.xhn.sys.userrole.service.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * 权限信息 服务实现类
 *
 * @author xhn
 * @date 2025-12-20 14:13:25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    private final SysUserRoleService sysUserRoleService;

    @Override
    public UserPermissionsDTO getUserPermissionsAndRoles(Long userId) {
        if (userId == null) {
            log.error("用户ID为空，无法获取权限和角色");
            return null;
        }

        // 获取用户角色
        List<SysRole> roles = sysUserRoleService.getRolesByUserId(userId);
        if (roles == null || roles.isEmpty()) {
            log.warn("用户未分配角色，userId: {}", userId);
            UserPermissionsDTO dto = new UserPermissionsDTO();
            dto.setUserId(userId);
            dto.setIsSuperAdmin(false);
            dto.setRoles(Collections.emptyList());
            dto.setPermissions(Collections.emptyList());
            return dto;
        }

        // 判断是否是超级管理员
        boolean isSuperAdmin = roles.stream()
                .anyMatch(role -> SecurityConstants.SUPER_ADMIN_ROLE_CODE.equals(role.getRoleCode()));

        List<SysPermission> permissions;
        if (isSuperAdmin) {
            // 超级管理员获取所有权限
            log.info("超级管理员获取所有权限，userId: {}", userId);
            permissions = baseMapper.selectList(null);
        } else {
            // 普通用户根据角色获取权限
            log.info("普通用户获取角色权限，userId: {}", userId);
            permissions = baseMapper.selectPermissionsByUserId(userId);
        }

        UserPermissionsDTO dto = new UserPermissionsDTO();
        dto.setUserId(userId);
        dto.setIsSuperAdmin(isSuperAdmin);
        dto.setRoles(roles);
        dto.setPermissions(permissions != null ? permissions : Collections.emptyList());

        return dto;
    }

    @Override
    public  Mono<UserPermissionsDTO> getCurrentUserPermissionsAndRoles() {
        return SecurityUtils.getCurrentUserId()
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("无法获取当前登录用户ID");
                    return Mono.empty();
                }))
                .map(this::getUserPermissionsAndRoles);
    }

}