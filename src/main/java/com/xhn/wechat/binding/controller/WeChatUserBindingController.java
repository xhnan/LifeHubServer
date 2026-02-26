package com.xhn.wechat.binding.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.response.ResponseResult;
import com.xhn.wechat.binding.model.BaseWeChatUserBinding;
import com.xhn.wechat.binding.service.WeChatUserBindingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 企业微信用户绑定控制器
 * @author xhn
 * @date 2026-02-26
 */
@RestController
@RequestMapping("/wechat/user-binding")
@RequiredArgsConstructor
@Tag(name = "企业微信用户绑定", description = "企业微信用户绑定管理")
public class WeChatUserBindingController {

    private final WeChatUserBindingService userBindingService;

    @GetMapping("/test")
    @Operation(summary = "测试接口")
    public ResponseResult<String> test() {
        return ResponseResult.success("WeChat user binding service is ready");
    }

    /**
     * 新增用户绑定
     * @param binding 用户绑定
     * @return 操作结果
     */
    @PostMapping
    @Operation(summary = "新增用户绑定")
    public ResponseResult<BaseWeChatUserBinding> create(@RequestBody BaseWeChatUserBinding binding) {
        boolean result = userBindingService.save(binding);
        if (result) {
            return ResponseResult.success(binding);
        }
        return ResponseResult.error("新增用户绑定失败");
    }

    /**
     * 根据ID删除用户绑定
     * @param id 绑定ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户绑定")
    public ResponseResult<Boolean> delete(@PathVariable Long id) {
        boolean result = userBindingService.removeById(id);
        if (result) {
            return ResponseResult.success(true);
        }
        return ResponseResult.error("删除用户绑定失败");
    }

    /**
     * 更新用户绑定
     * @param binding 用户绑定
     * @return 操作结果
     */
    @PutMapping
    @Operation(summary = "更新用户绑定")
    public ResponseResult<BaseWeChatUserBinding> update(@RequestBody BaseWeChatUserBinding binding) {
        boolean result = userBindingService.updateById(binding);
        if (result) {
            return ResponseResult.success(binding);
        }
        return ResponseResult.error("更新用户绑定失败");
    }

    /**
     * 根据ID查询用户绑定
     * @param id 绑定ID
     * @return 用户绑定
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询用户绑定")
    public ResponseResult<BaseWeChatUserBinding> getById(@PathVariable Long id) {
        BaseWeChatUserBinding binding = userBindingService.getById(id);
        if (binding != null) {
            return ResponseResult.success(binding);
        }
        return ResponseResult.error("用户绑定不存在");
    }

    /**
     * 查询所有用户绑定
     * @return 用户绑定列表
     */
    @GetMapping
    @Operation(summary = "查询所有用户绑定")
    public ResponseResult<List<BaseWeChatUserBinding>> listAll() {
        List<BaseWeChatUserBinding> list = userBindingService.list();
        return ResponseResult.success(list);
    }

    /**
     * 分页查询用户绑定
     * @param pageNum  页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询用户绑定")
    public ResponseResult<Page<BaseWeChatUserBinding>> pageAll(
            @RequestParam int pageNum,
            @RequestParam int pageSize) {
        Page<BaseWeChatUserBinding> page = new Page<>(pageNum, pageSize);
        Page<BaseWeChatUserBinding> resultPage = userBindingService.page(page);
        return ResponseResult.success(resultPage);
    }

    /**
     * 根据系统用户ID和应用ID查询绑定关系
     * @param userId 系统用户ID
     * @param appId  应用ID
     * @return 用户绑定
     */
    @GetMapping("/by-user-app")
    @Operation(summary = "根据用户ID和应用ID查询绑定")
    public ResponseResult<BaseWeChatUserBinding> getByUserIdAndAppId(
            @RequestParam Long userId,
            @RequestParam Long appId) {
        BaseWeChatUserBinding binding = userBindingService.getByUserIdAndAppId(userId, appId);
        if (binding != null) {
            return ResponseResult.success(binding);
        }
        return ResponseResult.error("用户绑定不存在");
    }

    /**
     * 根据企业微信UserID和应用ID查询绑定关系
     * @param wxUserId 企业微信UserID
     * @param appId    应用ID
     * @return 用户绑定
     */
    @GetMapping("/by-wxuser-app")
    @Operation(summary = "根据企业微信UserID和应用ID查询绑定")
    public ResponseResult<BaseWeChatUserBinding> getByWxUserIdAndAppId(
            @RequestParam String wxUserId,
            @RequestParam Long appId) {
        BaseWeChatUserBinding binding = userBindingService.getByWxUserIdAndAppId(wxUserId, appId);
        if (binding != null) {
            return ResponseResult.success(binding);
        }
        return ResponseResult.error("用户绑定不存在");
    }
}
