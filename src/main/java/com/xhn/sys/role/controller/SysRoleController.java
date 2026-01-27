package com.xhn.sys.role.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.util.concurrent.RateLimiter;
import com.xhn.response.ResponseResult;
import com.xhn.sys.permission.model.SysPermission;
import com.xhn.sys.role.model.SysRole;
import com.xhn.sys.role.service.SysRoleService;
import com.xhn.sys.rolepermission.service.SysRolePermissionService;
import com.xhn.sys.user.model.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 系统角色控制器
 *
 * @author xhn
 * @date 2025-12-23 10:21:30
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController {

    private Logger logger = LoggerFactory.getLogger(SysRoleController.class);

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysRolePermissionService sysRolePermissionService;

    /**
     * 新增角色
     *
     * @param sysRole 角色信息
     * @return 操作结果
     */
    @PostMapping
    public ResponseResult<SysRole> create(@RequestBody SysRole sysRole) {
        boolean result = sysRoleService.save(sysRole);
        if (result) {
            return ResponseResult.success(sysRole);
        } else {
            return ResponseResult.error("新增角色失败");
        }
    }

    /**
     * 根据ID删除角色
     *
     * @param id 角色ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id) {
        boolean result = sysRoleService.removeById(id);
        if (result) {
            return ResponseResult.success();
        } else {
            return ResponseResult.error("删除角色失败");
        }
    }

    /**
     * 更新角色信息
     *
     * @param sysRole 角色信息
     * @return 操作结果
     */
    @PutMapping
    public ResponseResult<SysRole> update(@RequestBody SysRole sysRole) {
        boolean result = sysRoleService.updateById(sysRole);
        if (result) {
            return ResponseResult.success(sysRole);
        } else {
            return ResponseResult.error("更新角色失败");
        }
    }

    /**
     * 根据ID查询角色
     *
     * @param id 角色ID
     * @return 角色信息
     */
    @GetMapping("/{id}")
    public ResponseResult<SysRole> getById(@PathVariable Long id) {
        SysRole sysRole = sysRoleService.getById(id);
        if (sysRole != null) {
            return ResponseResult.success(sysRole);
        } else {
            return ResponseResult.error("角色不存在");
        }
    }

    /**
     * 查询所有角色列表
     *
     * @return 角色列表
     */
    @GetMapping
    public ResponseResult<List<SysRole>> listAll() {
        List<SysRole> list = sysRoleService.list();
        return ResponseResult.success(list);
    }


    //分页全部用户
    @GetMapping("/page")
    public ResponseResult<Page<SysRole>> pageAll(@RequestParam int pageNum, @RequestParam int pageSize) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        Page<SysRole> resultPage = sysRoleService.page(page);
        return ResponseResult.success(resultPage);
    }

    @GetMapping("/test")
    public ResponseResult test() {

        logger.info("time----"+ new Date());
        return ResponseResult.success();
    }

    /**
     * 根据角色ID查询权限列表
     *
     * @param id 角色ID
     * @return 权限列表
     */
    @GetMapping("/{id}/permissions")
    public ResponseResult<List<SysPermission>> getPermissionsByRoleId(@PathVariable Long id) {
        List<SysPermission> permissions = sysRolePermissionService.getPermissionsByRoleId(id);
        return ResponseResult.success(permissions);
    }

}