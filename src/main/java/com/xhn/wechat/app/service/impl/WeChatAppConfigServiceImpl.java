package com.xhn.wechat.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhn.wechat.app.mapper.WeChatAppConfigMapper;
import com.xhn.wechat.app.model.BaseWeChatAppConfig;
import com.xhn.wechat.app.service.WeChatAppConfigService;
import org.springframework.stereotype.Service;

/**
 * 企业微信应用配置 服务实现类
 * @author xhn
 * @date 2026-02-26
 */
@Service
public class WeChatAppConfigServiceImpl extends ServiceImpl<WeChatAppConfigMapper, BaseWeChatAppConfig> implements WeChatAppConfigService {

    @Override
    public BaseWeChatAppConfig getByAgentId(Long agentId) {
        LambdaQueryWrapper<BaseWeChatAppConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseWeChatAppConfig::getAgentId, agentId)
                .eq(BaseWeChatAppConfig::getIsEnabled, 1);
        return getOne(wrapper);
    }
}
