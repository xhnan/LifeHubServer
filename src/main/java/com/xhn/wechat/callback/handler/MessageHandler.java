package com.xhn.wechat.callback.handler;

import com.xhn.wechat.app.model.BaseWeChatAppConfig;
import com.xhn.wechat.client.WeChatApiClient;
import com.xhn.wechat.client.dto.CallbackEvent;
import com.xhn.wechat.client.dto.SendMessageResponse;
import com.xhn.wechat.message.model.BaseWeChatMessage;
import com.xhn.wechat.message.service.WeChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 企业微信消息处理器
 * 处理各种消息和事件
 * @author xhn
 * @date 2026-02-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageHandler {

    private final WeChatApiClient weChatApiClient;
    private final WeChatMessageService messageService;

    /**
     * 处理消息和事件
     * @param event     回调事件
     * @param appConfig 应用配置
     * @return 回复内容（为空则不回复）
     */
    public String handleEvent(CallbackEvent event, BaseWeChatAppConfig appConfig) {
        try {
            String msgType = event.getMsgType();

            if ("text".equals(msgType)) {
                return handleTextMessage(event, appConfig);
            } else if ("event".equals(msgType)) {
                return handleEventTypeEvent(event, appConfig);
            } else if ("image".equals(msgType)) {
                return handleImageMessage(event, appConfig);
            } else if ("voice".equals(msgType)) {
                return handleVoiceMessage(event, appConfig);
            } else if ("video".equals(msgType)) {
                return handleVideoMessage(event, appConfig);
            } else if ("file".equals(msgType)) {
                return handleFileMessage(event, appConfig);
            } else {
                log.info("Unhandled message type: {}", msgType);
            }

            return null;
        } catch (Exception e) {
            log.error("Handle event error", e);
            return null;
        }
    }

    /**
     * 处理文本消息
     */
    private String handleTextMessage(CallbackEvent event, BaseWeChatAppConfig appConfig) {
        String content = event.getContent();
        String fromUser = event.getFromUserName();

        log.info("Received text message from {}: {}", fromUser, content);

        // 自动回复逻辑
        String reply = generateReply(content, fromUser);

        // 异步发送回复
        sendReplyAsync(appConfig, fromUser, reply);

        // 对于回调接口，返回success即可（实际回复通过API发送）
        return "success";
    }

    /**
     * 处理事件类型的事件
     */
    private String handleEventTypeEvent(CallbackEvent event, BaseWeChatAppConfig appConfig) {
        String eventType = event.getEvent();
        String fromUser = event.getFromUserName();

        log.info("Received event: {} from {}", eventType, fromUser);

        switch (eventType) {
            case "enter_agent":
                // 用户进入应用
                handleEnterAgent(event, appConfig);
                break;
            case "exit_agent":
                // 用户离开应用
                handleExitAgent(event, appConfig);
                break;
            case "click":
                // 点击菜单
                handleClickEvent(event, appConfig);
                break;
            case "subscribe":
                // 关注事件
                handleSubscribe(event, appConfig);
                break;
            case "unsubscribe":
                // 取消关注
                handleUnsubscribe(event, appConfig);
                break;
            default:
                log.info("Unhandled event type: {}", eventType);
                break;
        }

        return "success";
    }

    /**
     * 处理图片消息
     */
    private String handleImageMessage(CallbackEvent event, BaseWeChatAppConfig appConfig) {
        String fromUser = event.getFromUserName();
        String mediaId = event.getMediaId();

        log.info("Received image message from {}, mediaId: {}", fromUser, mediaId);

        return "success";
    }

    /**
     * 处理语音消息
     */
    private String handleVoiceMessage(CallbackEvent event, BaseWeChatAppConfig appConfig) {
        String fromUser = event.getFromUserName();
        String mediaId = event.getMediaId();

        log.info("Received voice message from {}, mediaId: {}", fromUser, mediaId);

        return "success";
    }

    /**
     * 处理视频消息
     */
    private String handleVideoMessage(CallbackEvent event, BaseWeChatAppConfig appConfig) {
        String fromUser = event.getFromUserName();
        String mediaId = event.getMediaId();

        log.info("Received video message from {}, mediaId: {}", fromUser, mediaId);

        return "success";
    }

    /**
     * 处理文件消息
     */
    private String handleFileMessage(CallbackEvent event, BaseWeChatAppConfig appConfig) {
        String fromUser = event.getFromUserName();
        String mediaId = event.getMediaId();

        log.info("Received file message from {}, mediaId: {}", fromUser, mediaId);

        return "success";
    }

    /**
     * 处理用户进入应用事件
     */
    private void handleEnterAgent(CallbackEvent event, BaseWeChatAppConfig appConfig) {
        String fromUser = event.getFromUserName();
        log.info("User {} entered agent {}", fromUser, appConfig.getAgentId());

        // 发送欢迎消息
        String welcomeMsg = "欢迎使用LifeHub！我是您的智能助手，可以帮您：\n\n" +
                "1. 查看财务数据\n" +
                "2. 记录交易\n" +
                "3. 查询账单\n" +
                "4. 预算提醒\n\n" +
                "输入'帮助'查看更多功能";

        sendReplyAsync(appConfig, fromUser, welcomeMsg);
    }

    /**
     * 处理用户离开应用事件
     */
    private void handleExitAgent(CallbackEvent event, BaseWeChatAppConfig appConfig) {
        String fromUser = event.getFromUserName();
        log.info("User {} exited agent {}", fromUser, appConfig.getAgentId());
    }

    /**
     * 处理点击菜单事件
     */
    private void handleClickEvent(CallbackEvent event, BaseWeChatAppConfig appConfig) {
        String fromUser = event.getFromUserName();
        String eventKey = event.getEventKey();

        log.info("User {} clicked menu: {}", fromUser, eventKey);

        String reply = handleMenuClick(eventKey);
        sendReplyAsync(appConfig, fromUser, reply);
    }

    /**
     * 处理关注事件
     */
    private void handleSubscribe(CallbackEvent event, BaseWeChatAppConfig appConfig) {
        String fromUser = event.getFromUserName();
        log.info("User {} subscribed", fromUser);
    }

    /**
     * 处理取消关注事件
     */
    private void handleUnsubscribe(CallbackEvent event, BaseWeChatAppConfig appConfig) {
        String fromUser = event.getFromUserName();
        log.info("User {} unsubscribed", fromUser);
    }

    /**
     * 生成自动回复内容
     */
    private String generateReply(String content, String fromUser) {
        content = content.trim();

        // 简单的关键词匹配
        if (content.contains("帮助") || content.contains("help")) {
            return "欢迎使用LifeHub企业微信助手！\n\n" +
                    "可用功能：\n" +
                    "• 输入'记录'快速记录交易\n" +
                    "• 输入'查询'查看财务数据\n" +
                    "• 输入'预算'查看预算情况\n" +
                    "• 输入'提醒'设置提醒\n\n" +
                    "更多功能开发中...";
        }

        if (content.contains("你好") || content.contains("hi") || content.contains("hello")) {
            return "您好！我是LifeHub智能助手，有什么可以帮您的？";
        }

        if (content.contains("记录")) {
            return "记录交易功能：\n\n" +
                    "请按以下格式输入：\n" +
                    "记录 [支出/收入] [金额] [分类] [备注]\n\n" +
                    "例如：记录 支出 50 餐饮 午餐";
        }

        if (content.contains("查询")) {
            return "财务查询功能：\n\n" +
                    "• 输入'本月支出'查看本月支出\n" +
                    "• 输入'本月收入'查看本月收入\n" +
                    "• 输入'账户余额'查看余额";
        }

        if (content.contains("预算")) {
            return "预算查询：\n\n" +
                    "正在为您查询预算信息...";
        }

        if (content.contains("提醒")) {
            return "提醒设置：\n\n" +
                    "• 输入'开启提醒'开启消息推送\n" +
                    "• 输入'关闭提醒'关闭消息推送";
        }

        // 默认回复
        return "收到您的消息：" + content + "\n\n" +
                "输入'帮助'查看可用功能。";
    }

    /**
     * 处理菜单点击
     */
    private String handleMenuClick(String eventKey) {
        switch (eventKey) {
            case "menu_today":
                return "今日财务数据：\n\n" +
                        "收入：¥0.00\n" +
                        "支出：¥0.00";
            case "menu_month":
                return "本月财务数据：\n\n" +
                        "收入：¥0.00\n" +
                        "支出：¥0.00";
            case "menu_record":
                return "快速记账：\n\n" +
                        "请输入：\n" +
                        "记录 [支出/收入] [金额] [分类] [备注]";
            case "menu_help":
                return "帮助中心\n\n" +
                        "这里是使用说明...";
            default:
                return "正在处理...";
        }
    }

    /**
     * 异步发送回复
     */
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
                    // 保存发送记录
                    saveSentMessage(appConfig.getId(), toUser, content, response);
                })
                .doOnError(e -> {
                    log.error("Send reply error", e);
                    // 保存失败记录
                    saveSentMessage(appConfig.getId(), toUser, content, null);
                })
                .subscribe();
    }

    /**
     * 保存发送消息记录
     */
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
