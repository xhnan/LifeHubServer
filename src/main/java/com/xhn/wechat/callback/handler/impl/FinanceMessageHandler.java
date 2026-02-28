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
 * 财务应用专用消息处理器
 * @author xhn
 * @date 2026-02-28
 */
@Slf4j
@Component("financeMessageHandler")
@RequiredArgsConstructor
public class FinanceMessageHandler implements MessageHandler {

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
        return "finance";
    }

    private String handleTextMessage(CallbackEvent event, BaseWeChatAppConfig appConfig) {
        String content = event.getContent();
        String fromUser = event.getFromUserName();

        log.info("Finance app received text from {}: {}", fromUser, content);

        String reply = generateFinanceReply(content);
        sendReplyAsync(appConfig, fromUser, reply);

        return "success";
    }

    private String handleEventTypeEvent(CallbackEvent event, BaseWeChatAppConfig appConfig) {
        String eventType = event.getEvent();
        String fromUser = event.getFromUserName();

        log.info("Finance app received event: {} from {}", eventType, fromUser);

        if ("enter_agent".equals(eventType)) {
            String welcomeMsg = "👋 欢迎使用 LifeHub 财务助手！\n\n" +
                    "📊 我可以帮您：\n" +
                    "• 输入'本月支出'查看本月支出\n" +
                    "• 输入'本月收入'查看本月收入\n" +
                    "• 输入'账户余额'查看余额\n" +
                    "• 输入'预算情况'查看预算\n\n" +
                    "💰 让财务管理更轻松！";

            sendReplyAsync(appConfig, fromUser, welcomeMsg);
        }

        return "success";
    }

    private String generateFinanceReply(String content) {
        content = content.trim();

        if (content.contains("本月支出")) {
            return "📊 本月支出统计\n\n" +
                    "总支出：¥5,230.50\n" +
                    "• 餐饮：¥1,200.00\n" +
                    "• 交通：¥580.00\n" +
                    "• 购物：¥2,100.50\n" +
                    "• 其他：¥1,350.00";
        }

        if (content.contains("本月收入")) {
            return "💰 本月收入统计\n\n" +
                    "总收入：¥15,000.00\n" +
                    "• 工资：¥12,000.00\n" +
                    "• 理财：¥2,500.00\n" +
                    "• 其他：¥500.00";
        }

        if (content.contains("余额")) {
            return "💳 账户余额\n\n" +
                    "• 招商银行：¥25,680.00\n" +
                    "• 支付宝：¥8,520.50\n" +
                    "• 微信零钱：¥1,230.00\n\n" +
                    "总计：¥35,430.50";
        }

        if (content.contains("预算")) {
            return "📈 预算执行情况\n\n" +
                    "本月预算：¥10,000.00\n" +
                    "已支出：¥5,230.50\n" +
                    "剩余：¥4,769.50\n\n" +
                    "预算使用率：52.3% ✅";
        }

        if (content.contains("帮助") || content.contains("help")) {
            return "📖 LifeHub 财务助手帮助\n\n" +
                    "常用命令：\n" +
                    "• 本月支出/收入\n" +
                    "• 账户余额\n" +
                    "• 预算情况\n" +
                    "• 记账 [金额] [分类]";
        }

        return "💬 我收到了：" + content + "\n\n" +
                "输入'帮助'查看可用功能";
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
