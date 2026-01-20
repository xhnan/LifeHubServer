package com.xhn.sys.userrole.controller;

import com.xhn.response.ResponseResult;
import com.xhn.sys.userrole.model.SysUserRole;
import com.xhn.sys.userrole.service.SysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统用户角色关联控制器
 *
 * @author xhn
 * @date 2025-12-19
 */
@RestController
@RequestMapping("/sys/userrole")
public class SysUserRoleController {

    @Autowired
    private SysUserRoleService sysUserRoleService;

    /**
     * 新增用户角色关联
     *
     * @param sysUserRole 用户角色关联信息
     * @return 操作结果
     */
    @PostMapping
    public ResponseResult<SysUserRole> add(@RequestBody SysUserRole sysUserRole) {
        boolean isSuccess = sysUserRoleService.save(sysUserRole);
        if (isSuccess) {
            return ResponseResult.success(sysUserRole);
        }
        return ResponseResult.error("新增失败");
    }

    /**
     * 根据ID删除用户角色关联
     *
     * @param id 主键ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id) {
        boolean isSuccess = sysUserRoleService.removeById(id);
        if (isSuccess) {
            return ResponseResult.success();
        }
        return ResponseResult.error("删除失败");
    }

    /**
     * 修改用户角色关联
     *
     * @param sysUserRole 用户角色关联信息
     * @return 操作结果
     */
    @PutMapping
    public ResponseResult<SysUserRole> update(@RequestBody SysUserRole sysUserRole) {
        boolean isSuccess = sysUserRoleService.updateById(sysUserRole);
        if (isSuccess) {
            return ResponseResult.success(sysUserRole);
        }
        return ResponseResult.error("修改失败");
    }

    /**
     * 根据ID查询用户角色关联
     *
     * @param id 主键ID
     * @return 查询结果
     */
    @GetMapping("/{id}")
    public ResponseResult<SysUserRole> getById(@PathVariable Long id) {
        SysUserRole sysUserRole = sysUserRoleService.getById(id);
        if (sysUserRole != null) {
            return ResponseResult.success(sysUserRole);
        }
        return ResponseResult.error("查询失败");
    }

    /**
     * 查询所有用户角色关联
     *
     * @return 查询结果
     */
    @GetMapping
    public ResponseResult<List<SysUserRole>> getAll() {
        List<SysUserRole> list = sysUserRoleService.list();
        return ResponseResult.success(list);
    }
}