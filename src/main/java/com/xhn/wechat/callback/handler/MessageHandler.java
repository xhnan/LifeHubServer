package com.xhn.wechat.callback.handler;

import com.xhn.wechat.app.model.BaseWeChatAppConfig;
import com.xhn.wechat.client.dto.CallbackEvent;

/**
 * 企业微信消息处理器接口
 * 不同应用可以有不同的实现
 * @author xhn
 * @date 2026-02-28
 */
public interface MessageHandler {

    /**
     * 处理消息和事件
     * @param event     回调事件
     * @param appConfig 应用配置
     * @return 回复内容（为空则不回复）
     */
    String handleEvent(CallbackEvent event, BaseWeChatAppConfig appConfig);

    /**
     * 获取支持的类型标识
     * @return 类型标识（如 "default", "finance", "hr"）
     */
    String getType();
}
