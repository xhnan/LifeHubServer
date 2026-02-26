package com.xhn.wechat.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xhn.wechat.message.model.BaseWeChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 企业微信消息 Mapper
 * @author xhn
 * @date 2026-02-26
 */
@Mapper
public interface WeChatMessageMapper extends BaseMapper<BaseWeChatMessage> {
}
