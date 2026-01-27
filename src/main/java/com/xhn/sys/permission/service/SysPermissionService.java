package com.xhn.sys.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.sys.permission.model.SysPermission;
import com.xhn.sys.permission.model.UserPermissionsDTO;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 权限表 服务接口
 *
 * @author xhn
 * @date 2025-12-20 10:13:25
 */
public interface SysPermissionService extends IService<SysPermission> {

    /**
     * 根据用户ID获取用户的权限和角色信息
     * 超级管理员返回全部权限
     *
     * @param userId 用户ID
     * @return 用户权限和角色信息
     */
    UserPermissionsDTO getUserPermissionsAndRoles(Long userId);

    /**
     * 获取当前登录用户的权限和角色信息
     * 超级管理员返回全部权限
     *
     * @return 用户权限和角色信息
     */
    Mono<UserPermissionsDTO> getCurrentUserPermissionsAndRoles();

}