package com.xhn.sys.user.controller;

import com.xhn.sys.user.model.SysUser;
import com.xhn.sys.user.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统用户控制器
 *
 * @author xhn
 * @date 2025-12-10 10:13:27
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 新增用户
     *
     * @param sysUser 用户信息
     * @return 操作结果
     */
    @PostMapping
    public Boolean save(@RequestBody SysUser sysUser) {
        return sysUserService.save(sysUser);
    }

    /**
     * 根据ID删除用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Boolean removeById(@PathVariable Long id) {
        return sysUserService.removeById(id);
    }

    /**
     * 修改用户信息
     *
     * @param sysUser 用户信息
     * @return 操作结果
     */
    @PutMapping
    public Boolean updateById(@RequestBody SysUser sysUser) {
        return sysUserService.updateById(sysUser);
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public SysUser getById(@PathVariable Long id) {
        return sysUserService.getById(id);
    }

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    @GetMapping
    public List<SysUser> list() {
        return sysUserService.list();
    }
}