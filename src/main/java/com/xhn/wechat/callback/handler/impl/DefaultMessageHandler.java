package com.xhn.wechat.callback.handler.impl;

import com.xhn.wechat.app.model.BaseWeChatAppConfig;
import com.xhn.wechat.callback.handler.MessageHandler;
import com.xhn.wechat.client.WeChatApiClient;
import com.xhn.wechat.client.dto.CallbackEvent;
import com.xhn.wechat.client.dto.SendMessageResponse;
import com.xhn.wechat.message.model.BaseWeChatMessage;
import com.xhn.wechat.message.service.WeChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 默认消息处理器
 * 通用的回复逻辑
 * @author xhn
 * @date 2026-02-28
 */
@Slf4j
@Component("defaultMessageHandler")
@RequiredArgsConstructor
public class DefaultMessageHandler implements MessageHandler {

    private final WeChatApiClient weChatApiClient;
    private final WeChatMessageService messageService;

    @Override
    public String handleEvent(CallbackEvent event, BaseWeChatAppConfig appConfig) {
        try {
            String msgType = event.getMsgType();

            if ("text".equals(msgType)) {
                return handleTextMessage(event, appConfig);
            } else if ("event".equals(msgType)) {
                return handleEventTypeEvent(event, appConfig);
            }

            return null;
        } catch (Exception e) {
            log.error("Handle event error", e);
            return null;
        }
    }

    @Override
    public String getType() {
        return "default";
    }

    private String handleTextMessage(CallbackEvent event, BaseWeChatAppConfig appConfig) {
        String content = event.getContent();
        String fromUser = event.getFromUserName();

        log.info("Received text message from {}: {}", fromUser, content);

        String reply = generateReply(content, fromUser);
        sendReplyAsync(appConfig, fromUser, reply);

        return "success";
    }

    private String handleEventTypeEvent(CallbackEvent event, BaseWeChatAppConfig appConfig) {
        String eventType = event.getEvent();
        String fromUser = event.getFromUserName();

        log.info("Received event: {} from {}", eventType, fromUser);

        if ("enter_agent".equals(eventType)) {
            handleEnterAgent(event, appConfig);
        }

        return "success";
    }

    private void handleEnterAgent(CallbackEvent event, BaseWeChatAppConfig appConfig) {
        String fromUser = event.getFromUserName();
        String welcomeMsg = getWelcomeMessage(appConfig);

        log.info("User {} entered agent {}, sending welcome", fromUser, appConfig.getAgentId());
        sendReplyAsync(appConfig, fromUser, welcomeMsg);
    }

    /**
     * 从应用配置中获取欢迎消息
     */
    private String getWelcomeMessage(BaseWeChatAppConfig appConfig) {
        // 可以从 app_config 的扩展字段获取
        // 这里暂时返回默认值
        return "欢迎使用 " + appConfig.getAppName() + "！\n\n" +
                "输入'帮助'查看可用功能。";
    }

    private String generateReply(String content, String fromUser) {
        content = content.trim();

        if (content.contains("帮助") || content.contains("help")) {
            return "欢迎使用 " + fromUser + "！\n\n" +
                    "可用功能：\n" +
                    "• 输入'你好'打个招呼\n" +
                    "• 输入'时间'查看当前时间\n" +
                    "• 更多功能开发中...";
        }

        if (content.contains("你好") || content.contains("hi")) {
            return "您好！我是智能助手，有什么可以帮您的？";
        }

        if (content.contains("时间")) {
            return "当前时间：" + new java.util.Date();
        }

        return "收到您的消息：" + content + "\n\n输入'帮助'查看可用功能。";
    }

    private void sendReplyAsync(BaseWeChatAppConfig appConfig, String toUser, String content) {
        weChatApiClient.getAccessToken(appConfig.getCorpId(), appConfig.getCorpSecret())
                .flatMap(accessToken -> {
                    return weChatApiClient.sendTextMessage(
                            accessToken,
                            appConfig.getAgentId(),
                            toUser,
                            content
                    );
                })
                .doOnNext(response -> {
                    saveSentMessage(appConfig.getId(), toUser, content, response);
                })
                .doOnError(e -> {
                    log.error("Send reply error", e);
                    saveSentMessage(appConfig.getId(), toUser, content, null);
                })
                .subscribe();
    }

    private void saveSentMessage(Long appId, String toUser, String content, SendMessageResponse response) {
        try {
            BaseWeChatMessage message = new BaseWeChatMessage();
            message.setAppId(appId);
            message.setMsgDirection("outbound");
            message.setMsgType("text");
            message.setToUser(toUser);
            message.setContent(content);

            if (response != null && response.getErrCode() == 0) {
                message.setStatus("success");
                message.setMsgId(response.getMsgId());
            } else {
                message.setStatus("failed");
                if (response != null) {
                    message.setErrorCode(String.valueOf(response.getErrCode()));
                    message.setErrorMsg(response.getErrMsg());
                }
            }

            messageService.save(message);
        } catch (Exception e) {
            log.error("Save sent message error", e);
        }
    }
}
