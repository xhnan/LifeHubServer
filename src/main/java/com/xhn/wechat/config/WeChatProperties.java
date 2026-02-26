package com.xhn.wechat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 企业微信配置属性
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat")
public class WeChatProperties {

    /**
     * 是否启用企业微信功能
     */
    private Boolean enabled = false;

    /**
     * 企业微信API基础URL
     */
    private String apiBaseUrl = "https://qyapi.weixin.qq.com/cgi-bin";

    /**
     * 回调URL前缀
     * 完整回调URL: {callbackUrlPrefix}/{agentId}
     */
    private String callbackUrlPrefix;
}
