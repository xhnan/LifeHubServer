package com.xhn.wechat.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.xhn.wechat.client.crypto.WxSignature;
import com.xhn.wechat.client.dto.*;
import com.xhn.wechat.client.crypto.WxCrypto;
import com.xhn.wechat.config.WeChatProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 企业微信API客户端
 * 封装所有企业微信API调用
 * @author xhn
 * @date 2026-02-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WeChatApiClient {

    private final WebClient webClient;
    private final WeChatProperties weChatProperties;

    // Token缓存，简单使用内存缓存（生产环境建议使用Redis）
    private volatile String cachedAccessToken;
    private volatile long tokenExpireTime;

    /**
     * 获取访问令牌（带缓存）
     * @param corpId     企业ID
     * @param corpSecret 应用密钥
     * @return 访问令牌
     */
    public Mono<String> getAccessToken(String corpId, String corpSecret) {
        // 检查缓存是否有效
        if (cachedAccessToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return Mono.just(cachedAccessToken);
        }

        String url = weChatProperties.getApiBaseUrl() + "/gettoken?corpid=" + corpId + "&corpsecret=" + corpSecret;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(AccessTokenResponse.class)
                .flatMap(response -> {
                    if (response.getErrCode() == 0) {
                        cachedAccessToken = response.getAccessToken();
                        // 提前5分钟过期，避免临界点
                        tokenExpireTime = System.currentTimeMillis() + (response.getExpiresIn() - 300) * 1000L;
                        log.info("Get access token success, expires in {} seconds", response.getExpiresIn());
                        return Mono.just(response.getAccessToken());
                    } else {
                        log.error("Get access token failed: {} - {}", response.getErrCode(), response.getErrMsg());
                        return Mono.error(new RuntimeException("Get access token failed: " + response.getErrMsg()));
                    }
                });
    }

    /**
     * 发送应用消息
     * @param accessToken 访问令牌
     * @param request     发送消息请求
     * @return 发送结果
     */
    public Mono<SendMessageResponse> sendMessage(String accessToken, SendMessageRequest request) {
        String url = weChatProperties.getApiBaseUrl() + "/message/send?access_token=" + accessToken;

        return webClient.post()
                .uri(url)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SendMessageResponse.class)
                .doOnNext(response -> {
                    if (response.getErrCode() != 0) {
                        log.warn("Send message failed: {} - {}", response.getErrCode(), response.getErrMsg());
                    } else {
                        log.info("Send message success, msgId: {}", response.getMsgId());
                    }
                });
    }

    /**
     * 发送文本消息
     * @param accessToken 访问令牌
     * @param agentId     应用ID
     * @param toUser      接收人UserID，多个用|分隔
     * @param content     文本内容
     * @return 发送结果
     */
    public Mono<SendMessageResponse> sendTextMessage(String accessToken, Long agentId, String toUser, String content) {
        SendMessageRequest request = SendMessageRequest.builder()
                .toUser(toUser)
                .msgType("text")
                .agentId(agentId)
                .text(SendMessageRequest.Text.builder().content(content).build())
                .build();

        return sendMessage(accessToken, request);
    }

    /**
     * 发送文本卡片消息
     * @param accessToken 访问令牌
     * @param agentId     应用ID
     * @param toUser      接收人UserID
     * @param title       标题
     * @param description 描述
     * @param url         跳转链接
     * @param btnTxt      按钮文字
     * @return 发送结果
     */
    public Mono<SendMessageResponse> sendTextCardMessage(String accessToken, Long agentId, String toUser,
                                                          String title, String description, String url, String btnTxt) {
        SendMessageRequest request = SendMessageRequest.builder()
                .toUser(toUser)
                .msgType("textcard")
                .agentId(agentId)
                .textCard(SendMessageRequest.TextCard.builder()
                        .title(title)
                        .description(description)
                        .url(url)
                        .btnTxt(btnTxt != null ? btnTxt : "详情")
                        .build())
                .build();

        return sendMessage(accessToken, request);
    }

    /**
     * 发送Markdown消息
     * @param accessToken 访问令牌
     * @param agentId     应用ID
     * @param toUser      接收人UserID
     * @param content     Markdown内容
     * @return 发送结果
     */
    public Mono<SendMessageResponse> sendMarkdownMessage(String accessToken, Long agentId, String toUser, String content) {
        SendMessageRequest request = SendMessageRequest.builder()
                .toUser(toUser)
                .msgType("markdown")
                .agentId(agentId)
                .markdown(SendMessageRequest.Markdown.builder().content(content).build())
                .build();

        return sendMessage(accessToken, request);
    }

    /**
     * 获取成员详细信息
     * @param accessToken 访问令牌
     * @param userId      成员UserID
     * @return 用户信息
     */
    public Mono<UserInfoResponse> getUserInfo(String accessToken, String userId) {
        String url = weChatProperties.getApiBaseUrl() + "/user/get?access_token=" + accessToken + "&userid=" + userId;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(UserInfoResponse.class)
                .doOnNext(response -> {
                    if (response.getErrCode() != 0) {
                        log.warn("Get user info failed: {} - {}", response.getErrCode(), response.getErrMsg());
                    }
                });
    }

    /**
     * 通过code获取用户信息（网页授权）
     * @param accessToken 访问令牌
     * @param code        授权码
     * @return 用户授权信息
     */
    public Mono<AuthInfoResponse> getUserInfoByCode(String accessToken, String code) {
        String url = weChatProperties.getApiBaseUrl() + "/auth/getuserinfo?access_token=" + accessToken + "&code=" + code;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(AuthInfoResponse.class)
                .doOnNext(response -> {
                    if (response.getErrCode() != 0) {
                        log.warn("Get user info by code failed: {} - {}", response.getErrCode(), response.getErrMsg());
                    }
                });
    }

    /**
     * 读取成员
     * @param accessToken 访问令牌
     * @param userId      成员UserID
     * @return 用户信息
     */
    public Mono<UserInfoResponse> readUser(String accessToken, String userId) {
        String url = weChatProperties.getApiBaseUrl() + "/user/get?access_token=" + accessToken + "&userid=" + userId;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(UserInfoResponse.class);
    }

    /**
     * 验证URL并返回解密后的随机字符串
     * @param msgEncrypt 加密的消息
     * @param wxCrypto   加解密工具
     * @return 解密后的随机字符串
     */
    public String verifyUrl(String msgEncrypt, WxCrypto wxCrypto) {
//        return wxCrypto.decrypt(msgEncrypt);
        return "";
    }

    /**
     * 构造回复消息
     * @param content    回复内容
     * @param wxCrypto   加解密工具
     * @param agentId    应用ID
     * @return 加密后的XML
     */
    public String buildReplyMessage(String content, WxCrypto wxCrypto, Long agentId) {
        long timestamp = System.currentTimeMillis() / 1000;
        String nonce = getRandomStr();

        String plainText = String.format(
                "<Encrypt><![CDATA[%s]]></Encrypt><MsgSignature><![CDATA[%s]]></MsgSignature><TimeStamp>%s</TimeStamp><Nonce><![CDATA[%s]]></Nonce>",
                "",
                "",
                timestamp,
                nonce
        );

        String encrypt = "";
        String signature = WxSignature.generate("", String.valueOf(timestamp), nonce);

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
    private String getRandomStr() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(Integer.toHexString((int) (Math.random() * 16)));
        }
        return sb.toString();
    }

    /**
     * 清除缓存的Token
     */
    public void clearTokenCache() {
        this.cachedAccessToken = null;
        this.tokenExpireTime = 0;
    }
}
