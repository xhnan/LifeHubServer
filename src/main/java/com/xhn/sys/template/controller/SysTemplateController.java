package com.xhn.sys.template.controller;

import com.xhn.sys.template.model.SysTemplate;
import com.xhn.sys.template.service.SysTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统模板控制器
 * 
 * @author xhn
 * @date 2025-12-10 10:12:18
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
     * @return 新增结果
     */
    @PostMapping
    public boolean add(@RequestBody SysTemplate sysTemplate) {
        return sysTemplateService.save(sysTemplate);
    }

    /**
     * 根据ID删除模板
     * 
     * @param id 模板ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return sysTemplateService.removeById(id);
    }

    /**
     * 修改模板
     * 
     * @param sysTemplate 模板信息
     * @return 修改结果
     */
    @PutMapping
    public boolean update(@RequestBody SysTemplate sysTemplate) {
        return sysTemplateService.updateById(sysTemplate);
    }

    /**
     * 根据ID查询模板
     * 
     * @param id 模板ID
     * @return 模板信息
     */
    @GetMapping("/{id}")
    public SysTemplate getById(@PathVariable Long id) {
        return sysTemplateService.getById(id);
    }

    /**
     * 查询所有模板列表
     * 
     * @return 模板列表
     */
    @GetMapping
    public List<SysTemplate> list() {
        return sysTemplateService.list();
    }
}