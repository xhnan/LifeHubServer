package com.xhn.sys.template.controller;

import com.xhn.response.ResponseResult;
import com.xhn.sys.template.model.SysTemplate;
import com.xhn.sys.template.service.SysTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统模板控制器
 * 
 * @author xhn
 * @date 2025-12-10
 */
@RestController
@RequestMapping("/sys/template")
public class SysTemplateController {

    @Autowired
    private SysTemplateService sysTemplateService;

    /**
     * 新增模板
     * 
     * @param sysTemplate 模板信息
     * @return 操作结果
     */
    @PostMapping
    public ResponseResult<SysTemplate> create(@RequestBody SysTemplate sysTemplate) {
        boolean isSuccess = sysTemplateService.save(sysTemplate);
        if (isSuccess) {
            return ResponseResult.success(sysTemplate);
        }
        return ResponseResult.error("新增模板失败");
    }

    /**
     * 根据ID删除模板
     * 
     * @param id 模板ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseResult<Boolean> delete(@PathVariable Long id) {
        boolean isSuccess = sysTemplateService.removeById(id);
        if (isSuccess) {
            return ResponseResult.success(true);
        }
        return ResponseResult.error("删除模板失败");
    }

    /**
     * 更新模板
     * 
     * @param sysTemplate 模板信息
     * @return 操作结果
     */
    @PutMapping
    public ResponseResult<SysTemplate> update(@RequestBody SysTemplate sysTemplate) {
        boolean isSuccess = sysTemplateService.updateById(sysTemplate);
        if (isSuccess) {
            return ResponseResult.success(sysTemplate);
        }
        return ResponseResult.error("更新模板失败");
    }

    /**
     * 根据ID查询模板
     * 
     * @param id 模板ID
     * @return 模板信息
     */
    @GetMapping("/{id}")
    public ResponseResult<SysTemplate> getById(@PathVariable Long id) {
        SysTemplate sysTemplate = sysTemplateService.getById(id);
        if (sysTemplate != null) {
            return ResponseResult.success(sysTemplate);
        }
        return ResponseResult.error("模板不存在");
    }
}