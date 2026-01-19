package com.xhn.sys.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.base.exception.ApplicationException;
import com.xhn.response.ResponseResult;
import com.xhn.sys.user.model.SysUser;
import com.xhn.sys.user.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统用户控制器
 *
 * @author xhn
 * @date 2025-12-09
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/test")
    public ResponseResult<String>  test(){
        return ResponseResult.success("test");
    }



    /**
     * 新增用户
     *
     * @param sysUser 用户信息
     * @return 操作结果
     */
    @PostMapping
    public ResponseResult<SysUser> create(@RequestBody SysUser sysUser) {

        //对密码加密
        if (sysUser.getPassword() != null) {
            String password = sysUser.getPassword();
            //加入Security的加密
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            String newPassword =encoder.encode(password);
            sysUser.setPassword(newPassword);

        }else {
            throw new ApplicationException("密码不能为空");
        }


        boolean result = sysUserService.save(sysUser);
        if (result) {
            return ResponseResult.success(sysUser);
        }
        return ResponseResult.error("新增用户失败");
    }

    /**
     * 根据ID删除用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Boolean> delete(@PathVariable Long id) {
        boolean result = sysUserService.removeById(id);
        if (result) {
            return ResponseResult.success(true);
        }
        return ResponseResult.error("删除用户失败");
    }

    /**
     * 更新用户信息
     *
     * @param sysUser 用户信息
     * @return 操作结果
     */
    @PutMapping
    public ResponseResult<SysUser> update(@RequestBody SysUser sysUser) {
        boolean result = sysUserService.updateById(sysUser);
        if (result) {
            return ResponseResult.success(sysUser);
        }
        return ResponseResult.error("更新用户失败");
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public ResponseResult<SysUser> getById(@PathVariable Long id) {
        SysUser sysUser = sysUserService.getById(id);
        if (sysUser != null) {
            return ResponseResult.success(sysUser);
        }
        return ResponseResult.error("用户不存在");
    }

    /**
     * 查询所有用户列表
     *
     * @return 用户列表
     */
    @GetMapping
    public ResponseResult<List<SysUser>> listAll() {
        List<SysUser> list = sysUserService.list();
        return ResponseResult.success(list);
    }

    //分页全部用户
    @GetMapping("/page")
    public ResponseResult<Page<SysUser>> pageAll(@RequestParam int pageNum, @RequestParam int pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        Page<SysUser> resultPage = sysUserService.page(page);
        return ResponseResult.success(resultPage);
    }

}