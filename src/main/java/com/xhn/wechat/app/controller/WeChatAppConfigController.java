package com.xhn.wechat.app.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.response.ResponseResult;
import com.xhn.wechat.app.model.BaseWeChatAppConfig;
import com.xhn.wechat.app.service.WeChatAppConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 企业微信应用配置控制器
 * @author xhn
 * @date 2026-02-26
 */
@RestController
@RequestMapping("/wechat/app-config")
@RequiredArgsConstructor
@Tag(name = "企业微信应用配置", description = "企业微信应用配置管理")
public class WeChatAppConfigController {

    private final WeChatAppConfigService appConfigService;

    @GetMapping("/test")
    @Operation(summary = "测试接口")
    public ResponseResult<String> test() {
        return ResponseResult.success("WeChat app config service is ready");
    }

    /**
     * 新增应用配置
     * @param config 应用配置
     * @return 操作结果
     */
    @PostMapping
    @Operation(summary = "新增应用配置")
    public ResponseResult<BaseWeChatAppConfig> create(@RequestBody BaseWeChatAppConfig config) {
        boolean result = appConfigService.save(config);
        if (result) {
            return ResponseResult.success(config);
        }
        return ResponseResult.error("新增应用配置失败");
    }

    /**
     * 根据ID删除应用配置
     * @param id 配置ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除应用配置")
    public ResponseResult<Boolean> delete(@PathVariable Long id) {
        boolean result = appConfigService.removeById(id);
        if (result) {
            return ResponseResult.success(true);
        }
        return ResponseResult.error("删除应用配置失败");
    }

    /**
     * 更新应用配置
     * @param config 应用配置
     * @return 操作结果
     */
    @PutMapping
    @Operation(summary = "更新应用配置")
    public ResponseResult<BaseWeChatAppConfig> update(@RequestBody BaseWeChatAppConfig config) {
        boolean result = appConfigService.updateById(config);
        if (result) {
            return ResponseResult.success(config);
        }
        return ResponseResult.error("更新应用配置失败");
    }

    /**
     * 根据ID查询应用配置
     * @param id 配置ID
     * @return 应用配置
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询应用配置")
    public ResponseResult<BaseWeChatAppConfig> getById(@PathVariable Long id) {
        BaseWeChatAppConfig config = appConfigService.getById(id);
        if (config != null) {
            return ResponseResult.success(config);
        }
        return ResponseResult.error("应用配置不存在");
    }

    /**
     * 查询所有应用配置
     * @return 应用配置列表
     */
    @GetMapping
    @Operation(summary = "查询所有应用配置")
    public ResponseResult<List<BaseWeChatAppConfig>> listAll() {
        List<BaseWeChatAppConfig> list = appConfigService.list();
        return ResponseResult.success(list);
    }

    /**
     * 分页查询应用配置
     * @param pageNum  页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询应用配置")
    public ResponseResult<Page<BaseWeChatAppConfig>> pageAll(
            @RequestParam int pageNum,
            @RequestParam int pageSize) {
        Page<BaseWeChatAppConfig> page = new Page<>(pageNum, pageSize);
        Page<BaseWeChatAppConfig> resultPage = appConfigService.page(page);
        return ResponseResult.success(resultPage);
    }
}
