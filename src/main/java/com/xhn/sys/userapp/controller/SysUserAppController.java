package com.xhn.sys.userapp.controller;

import com.xhn.response.ResponseResult;
import com.xhn.sys.userapp.model.SysUserApp;
import com.xhn.sys.userapp.service.SysUserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户应用关联控制器
 * 
 * @author xhn
 * @date 2025-12-19
 */
@RestController
@RequestMapping("/sys/userapp")
public class SysUserAppController {

    @Autowired
    private SysUserAppService sysUserAppService;

    /**
     * 新增用户应用关联
     * 
     * @param sysUserApp 用户应用关联实体
     * @return 操作结果
     */
    @PostMapping
    public ResponseResult<SysUserApp> add(@RequestBody SysUserApp sysUserApp) {
        boolean result = sysUserAppService.save(sysUserApp);
        if (result) {
            return ResponseResult.success(sysUserApp);
        } else {
            return ResponseResult.error("新增失败");
        }
    }

    /**
     * 根据ID删除用户应用关联
     * 
     * @param id 主键ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id) {
        boolean result = sysUserAppService.removeById(id);
        if (result) {
            return ResponseResult.success();
        } else {
            return ResponseResult.error("删除失败");
        }
    }

    /**
     * 修改用户应用关联
     * 
     * @param sysUserApp 用户应用关联实体
     * @return 操作结果
     */
    @PutMapping
    public ResponseResult<SysUserApp> update(@RequestBody SysUserApp sysUserApp) {
        boolean result = sysUserAppService.updateById(sysUserApp);
        if (result) {
            return ResponseResult.success(sysUserApp);
        } else {
            return ResponseResult.error("修改失败");
        }
    }

    /**
     * 根据ID查询用户应用关联
     * 
     * @param id 主键ID
     * @return 查询结果
     */
    @GetMapping("/{id}")
    public ResponseResult<SysUserApp> getById(@PathVariable Long id) {
        SysUserApp sysUserApp = sysUserAppService.getById(id);
        if (sysUserApp != null) {
            return ResponseResult.success(sysUserApp);
        } else {
            return ResponseResult.error("查询失败，数据不存在");
        }
    }

    /**
     * 查询所有用户应用关联
     * 
     * @return 查询结果
     */
    @GetMapping
    public ResponseResult<List<SysUserApp>> listAll() {
        List<SysUserApp> list = sysUserAppService.list();
        return ResponseResult.success(list);
    }
}