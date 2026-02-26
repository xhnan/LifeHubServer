package com.xhn.wechat.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xhn.wechat.app.model.BaseWeChatAppConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 企业微信应用配置 Mapper
 * @author xhn
 * @date 2026-02-26
 */
@Mapper
public interface WeChatAppConfigMapper extends BaseMapper<BaseWeChatAppConfig> {
}
