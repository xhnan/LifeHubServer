package com.xhn.sys.app.controller;

import com.xhn.response.ResponseResult;
import com.xhn.sys.app.model.SysApp;
import com.xhn.sys.app.service.SysAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应用管理控制器
 *
 * @author xhn
 * @date 2025-12-23 10:19:20
 */
@RestController
@RequestMapping("/sys/app")
public class SysAppController {

    @Autowired
    private SysAppService sysAppService;

    /**
     * 新增应用
     *
     * @param sysApp 应用信息
     * @return 操作结果
     */
    @PostMapping
    public ResponseResult<Boolean> add(@RequestBody SysApp sysApp) {
        boolean result = sysAppService.save(sysApp);
        return result ? ResponseResult.success(true) : ResponseResult.error("新增失败");
    }

    /**
     * 根据ID删除应用
     *
     * @param id 应用ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Boolean> delete(@PathVariable Long id) {
        boolean result = sysAppService.removeById(id);
        return result ? ResponseResult.success(true) : ResponseResult.error("删除失败");
    }

    /**
     * 修改应用
     *
     * @param sysApp 应用信息
     * @return 操作结果
     */
    @PutMapping
    public ResponseResult<Boolean> update(@RequestBody SysApp sysApp) {
        boolean result = sysAppService.updateById(sysApp);
        return result ? ResponseResult.success(true) : ResponseResult.error("修改失败");
    }

    /**
     * 根据ID查询应用
     *
     * @param id 应用ID
     * @return 应用信息
     */
    @GetMapping("/{id}")
    public ResponseResult<SysApp> getById(@PathVariable Long id) {
        SysApp sysApp = sysAppService.getById(id);
        return sysApp != null ? ResponseResult.success(sysApp) : ResponseResult.error("查询失败");
    }

    /**
     * 查询所有应用列表
     *
     * @return 应用列表
     */
    @GetMapping
    public ResponseResult<List<SysApp>> list() {
        List<SysApp> list = sysAppService.list();
        return ResponseResult.success(list);
    }
}