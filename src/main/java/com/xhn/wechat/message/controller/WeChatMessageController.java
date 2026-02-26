package com.xhn.wechat.message.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.response.ResponseResult;
import com.xhn.wechat.message.model.BaseWeChatMessage;
import com.xhn.wechat.message.service.WeChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 企业微信消息控制器
 * @author xhn
 * @date 2026-02-26
 */
@RestController
@RequestMapping("/wechat/message")
@RequiredArgsConstructor
@Tag(name = "企业微信消息", description = "企业微信消息管理")
public class WeChatMessageController {

    private final WeChatMessageService messageService;

    @GetMapping("/test")
    @Operation(summary = "测试接口")
    public ResponseResult<String> test() {
        return ResponseResult.success("WeChat message service is ready");
    }

    /**
     * 根据ID删除消息记录
     * @param id 消息ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除消息记录")
    public ResponseResult<Boolean> delete(@PathVariable Long id) {
        boolean result = messageService.removeById(id);
        if (result) {
            return ResponseResult.success(true);
        }
        return ResponseResult.error("删除消息记录失败");
    }

    /**
     * 根据ID查询消息
     * @param id 消息ID
     * @return 消息记录
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询消息")
    public ResponseResult<BaseWeChatMessage> getById(@PathVariable Long id) {
        BaseWeChatMessage message = messageService.getById(id);
        if (message != null) {
            return ResponseResult.success(message);
        }
        return ResponseResult.error("消息记录不存在");
    }

    /**
     * 查询所有消息
     * @return 消息列表
     */
    @GetMapping
    @Operation(summary = "查询所有消息")
    public ResponseResult<List<BaseWeChatMessage>> listAll() {
        List<BaseWeChatMessage> list = messageService.list();
        return ResponseResult.success(list);
    }

    /**
     * 分页查询消息
     * @param pageNum  页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询消息")
    public ResponseResult<Page<BaseWeChatMessage>> pageAll(
            @RequestParam int pageNum,
            @RequestParam int pageSize) {
        Page<BaseWeChatMessage> page = new Page<>(pageNum, pageSize);
        Page<BaseWeChatMessage> resultPage = messageService.page(page);
        return ResponseResult.success(resultPage);
    }

    /**
     * 根据应用ID查询消息
     * @param appId 应用ID
     * @return 消息列表
     */
    @GetMapping("/by-app/{appId}")
    @Operation(summary = "根据应用ID查询消息")
    public ResponseResult<List<BaseWeChatMessage>> listByAppId(@PathVariable Long appId) {
        LambdaQueryWrapper<BaseWeChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseWeChatMessage::getAppId, appId)
                .orderByDesc(BaseWeChatMessage::getCreatedAt);
        List<BaseWeChatMessage> list = messageService.list(wrapper);
        return ResponseResult.success(list);
    }

    /**
     * 根据用户ID查询消息
     * @param userId 用户ID
     * @return 消息列表
     */
    @GetMapping("/by-user/{userId}")
    @Operation(summary = "根据用户ID查询消息")
    public ResponseResult<List<BaseWeChatMessage>> listByUserId(@PathVariable String userId) {
        LambdaQueryWrapper<BaseWeChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(BaseWeChatMessage::getFromUser, userId)
                        .or()
                        .eq(BaseWeChatMessage::getToUser, userId))
                .orderByDesc(BaseWeChatMessage::getCreatedAt);
        List<BaseWeChatMessage> list = messageService.list(wrapper);
        return ResponseResult.success(list);
    }

    /**
     * 根据消息方向查询
     * @param direction 消息方向 (inbound/outbound)
     * @return 消息列表
     */
    @GetMapping("/by-direction/{direction}")
    @Operation(summary = "根据消息方向查询")
    public ResponseResult<List<BaseWeChatMessage>> listByDirection(@PathVariable String direction) {
        LambdaQueryWrapper<BaseWeChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseWeChatMessage::getMsgDirection, direction)
                .orderByDesc(BaseWeChatMessage::getCreatedAt);
        List<BaseWeChatMessage> list = messageService.list(wrapper);
        return ResponseResult.success(list);
    }
}
