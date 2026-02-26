package com.xhn.wechat.callback.controller;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.xhn.wechat.app.model.BaseWeChatAppConfig;
import com.xhn.wechat.app.service.WeChatAppConfigService;
import com.xhn.wechat.callback.handler.MessageHandler;
import com.xhn.wechat.client.crypto.WxCrypto;
import com.xhn.wechat.client.crypto.WxSignature;
import com.xhn.wechat.client.dto.CallbackEvent;
import com.xhn.wechat.message.model.BaseWeChatMessage;
import com.xhn.wechat.message.service.WeChatMessageService;
import com.xhn.response.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 企业微信回调控制器
 * 处理企业微信的回调验证和消息接收
 * @author xhn
 * @date 2026-02-26
 */
@Slf4j
@RestController
@RequestMapping("/wechat/callback")
@RequiredArgsConstructor
@Tag(name = "企业微信回调", description = "企业微信消息回调接口")
public class WeChatCallbackController {

    private final WeChatAppConfigService appConfigService;
    private final MessageHandler messageHandler;
    private final WeChatMessageService messageService;
    private final XmlMapper xmlMapper = new XmlMapper();

    /**
     * 验证回调URL
     * 企业微信后台配置回调URL时会发送GET请求验证
     * @param agentId   应用ID
     * @param msgSignature 签名
     * @param timestamp  时间戳
     * @param nonce      随机字符串
     * @param echostr    加密的随机字符串
     * @return 解密后的随机字符串
     */
    @GetMapping(value = "/{agentId}", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "验证回调URL", description = "企业微信后台验证回调URL时调用")
    public Mono<String> verifyUrl(
            @PathVariable Long agentId,
            @RequestParam("msg_signature") String msgSignature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestParam("echostr") String echostr) {

        log.info("Verify URL request - agentId: {}, timestamp: {}, nonce: {}", agentId, timestamp, nonce);

        return Mono.fromCallable(() -> {
            // 获取应用配置
            BaseWeChatAppConfig appConfig = appConfigService.getByAgentId(agentId);
            if (appConfig == null) {
                log.error("App config not found for agentId: {}", agentId);
                throw new RuntimeException("App config not found");
            }

            // 验证签名
            String signature = WxSignature.generate(appConfig.getToken(), timestamp, nonce);
            if (!signature.equals(msgSignature)) {
                log.error("Signature verification failed");
                throw new RuntimeException("Signature verification failed");
            }

            // 解密echostr
            WxCrypto wxCrypto = new WxCrypto(appConfig.getToken(), appConfig.getEncodingAesKey());
            String result = wxCrypto.decrypt(echostr);

            log.info("Verify URL success for agentId: {}", agentId);
            return result;
        });
    }

    /**
     * 接收企业微信消息和事件回调
     * @param agentId   应用ID
     * @param msgSignature 签名
     * @param timestamp  时间戳
     * @param nonce      随机字符串
     * @param exchange   ServerWebExchange
     * @return 回复消息
     */
    @PostMapping(value = "/{agentId}", produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "接收消息和事件", description = "企业微信消息和事件回调")
    public Mono<String> receiveMessage(
            @PathVariable Long agentId,
            @RequestParam("msg_signature") String msgSignature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            ServerHttpRequest request) {

        log.info("Receive message callback - agentId: {}, timestamp: {}", agentId, timestamp);

        return DataBufferUtils.join(request.getBody())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .flatMap(body -> {
                    log.debug("Received callback body: {}", body);

                    return Mono.fromCallable(() -> {
                        // 获取应用配置
                        BaseWeChatAppConfig appConfig = appConfigService.getByAgentId(agentId);
                        if (appConfig == null) {
                            log.error("App config not found for agentId: {}", agentId);
                            throw new RuntimeException("App config not found");
                        }

                        // 验证签名
                        String signature = WxSignature.generate(appConfig.getToken(), timestamp, nonce);
                        if (!signature.equals(msgSignature)) {
                            log.error("Signature verification failed");
                            throw new RuntimeException("Signature verification failed");
                        }

                        // 解析XML
                        CallbackXml callbackXml = xmlMapper.readValue(body, CallbackXml.class);

                        // 解密消息
                        WxCrypto wxCrypto = new WxCrypto(appConfig.getToken(), appConfig.getEncodingAesKey());
                        String decryptedMsg = wxCrypto.decrypt(callbackXml.getEncrypt());

                        log.info("Decrypted message: {}", decryptedMsg);

                        // 解析解密后的消息
                        CallbackEvent event = xmlMapper.readValue(decryptedMsg, CallbackEvent.class);

                        // 保存接收到的消息
                        saveReceivedMessage(appConfig.getId(), event);

                        // 处理消息并生成回复
                        String replyContent = messageHandler.handleEvent(event, appConfig);

                        if (replyContent != null && !replyContent.isEmpty()) {
                            // 构造回复消息
                            return buildReply(replyContent, wxCrypto);
                        }

                        // 返回success表示已处理
                        return "success";
                    }).onErrorResume(e -> {
                        log.error("Handle message error", e);
                        return Mono.just("success");
                    });
                });
    }

    /**
     * 保存接收到的消息
     */
    private void saveReceivedMessage(Long appId, CallbackEvent event) {
        try {
            BaseWeChatMessage message = new BaseWeChatMessage();
            message.setAppId(appId);
            message.setMsgDirection("inbound");
            message.setMsgType(event.getMsgType());
            message.setFromUser(event.getFromUserName());
            message.setToUser(event.getToUserName());
            message.setMsgId(event.getMsgId());
            message.setStatus("success");

            // 根据消息类型设置内容
            if ("text".equals(event.getMsgType())) {
                message.setContent(event.getContent());
            } else if ("event".equals(event.getMsgType())) {
                message.setContent("Event: " + event.getEvent());
                if (event.getEventKey() != null) {
                    message.setContent(message.getContent() + ", Key: " + event.getEventKey());
                }
            } else {
                message.setContent(event.getMsgType());
            }

            messageService.save(message);
        } catch (Exception e) {
            log.error("Save received message error", e);
        }
    }

    /**
     * 构造回复消息
     */
    private String buildReply(String content, WxCrypto wxCrypto) {
        long timestamp = System.currentTimeMillis() / 1000;
        String nonce = getNonce();

        // 加密消息
        String encrypt = wxCrypto.encrypt(content, "");
        String signature = WxSignature.generate(wxCrypto.getToken(), String.valueOf(timestamp), nonce);

        return String.format(
                "<xml><Encrypt><![CDATA[%s]]></Encrypt><MsgSignature><![CDATA[%s]]></MsgSignature><TimeStamp>%s</TimeStamp><Nonce><![CDATA[%s]]></Nonce></xml>",
                encrypt,
                signature,
                timestamp,
                nonce
        );
    }

    /**
     * 获取随机字符串
     */
    private String getNonce() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(Integer.toHexString((int) (Math.random() * 16)));
        }
        return sb.toString();
    }

    /**
     * 回调XML内部类
     */
    @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement(localName = "xml")
    public static class CallbackXml {
        @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty(localName = "ToUserName")
        private String toUserName;

        @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty(localName = "FromUserName")
        private String fromUserName;

        @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty(localName = "CreateTime")
        private Long createTime;

        @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty(localName = "MsgType")
        private String msgType;

        @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty(localName = "Encrypt")
        private String encrypt;

        public String getToUserName() {
            return toUserName;
        }

        public void setToUserName(String toUserName) {
            this.toUserName = toUserName;
        }

        public String getFromUserName() {
            return fromUserName;
        }

        public void setFromUserName(String fromUserName) {
            this.fromUserName = fromUserName;
        }

        public Long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Long createTime) {
            this.createTime = createTime;
        }

        public String getMsgType() {
            return msgType;
        }

        public void setMsgType(String msgType) {
            this.msgType = msgType;
        }

        public String getEncrypt() {
            return encrypt;
        }

        public void setEncrypt(String encrypt) {
            this.encrypt = encrypt;
        }
    }
}
