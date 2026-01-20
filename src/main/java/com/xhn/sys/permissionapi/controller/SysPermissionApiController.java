package com.xhn.sys.permissionapi.controller;

import com.xhn.response.ResponseResult;
import com.xhn.sys.permissionapi.model.SysPermissionApi;
import com.xhn.sys.permissionapi.service.SysPermissionApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限API控制器
 *
 * @author xhn
 * @date 2025-12-23
 */
@RestController
@RequestMapping("/sys/permissionapi")
public class SysPermissionApiController {

    @Autowired
    private SysPermissionApiService sysPermissionApiService;

    /**
     * 新增权限API
     *
     * @param sysPermissionApi 权限API实体
     * @return 操作结果
     */
    @PostMapping
    public ResponseResult<SysPermissionApi> add(@RequestBody SysPermissionApi sysPermissionApi) {
        boolean isSuccess = sysPermissionApiService.save(sysPermissionApi);
        if (isSuccess) {
            return ResponseResult.success(sysPermissionApi);
        } else {
            return ResponseResult.error("新增失败");
        }
    }

    /**
     * 根据ID删除权限API
     *
     * @param id 主键ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Boolean> delete(@PathVariable Long id) {
        boolean isSuccess = sysPermissionApiService.removeById(id);
        if (isSuccess) {
            return ResponseResult.success(true);
        } else {
            return ResponseResult.error("删除失败");
        }
    }

    /**
     * 修改权限API
     *
     * @param sysPermissionApi 权限API实体
     * @return 操作结果
     */
    @PutMapping
    public ResponseResult<SysPermissionApi> update(@RequestBody SysPermissionApi sysPermissionApi) {
        boolean isSuccess = sysPermissionApiService.updateById(sysPermissionApi);
        if (isSuccess) {
            return ResponseResult.success(sysPermissionApi);
        } else {
            return ResponseResult.error("修改失败");
        }
    }

    /**
     * 根据ID查询权限API
     *
     * @param id 主键ID
     * @return 查询结果
     */
    @GetMapping("/{id}")
    public ResponseResult<SysPermissionApi> getById(@PathVariable Long id) {
        SysPermissionApi sysPermissionApi = sysPermissionApiService.getById(id);
        if (sysPermissionApi != null) {
            return ResponseResult.success(sysPermissionApi);
        } else {
            return ResponseResult.error("查询失败，数据不存在");
        }
    }

    /**
     * 查询所有权限API
     *
     * @return 查询结果
     */
    @GetMapping
    public ResponseResult<List<SysPermissionApi>> getAll() {
        List<SysPermissionApi> list = sysPermissionApiService.list();
        return ResponseResult.success(list);
    }
}