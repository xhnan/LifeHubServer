package com.xhn.wechat.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.wechat.app.model.BaseWeChatAppConfig;

/**
 * 企业微信应用配置 服务接口
 * @author xhn
 * @date 2026-02-26
 */
public interface WeChatAppConfigService extends IService<BaseWeChatAppConfig> {

    /**
     * 根据agentId获取应用配置
     * @param agentId 企业微信应用AgentId
     * @return 应用配置
     */
    BaseWeChatAppConfig getByAgentId(Long agentId);
}
