package com.xhn.wechat.message.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhn.wechat.message.mapper.WeChatMessageMapper;
import com.xhn.wechat.message.model.BaseWeChatMessage;
import com.xhn.wechat.message.service.WeChatMessageService;
import org.springframework.stereotype.Service;

/**
 * 企业微信消息 服务实现类
 * @author xhn
 * @date 2026-02-26
 */
@Service
public class WeChatMessageServiceImpl extends ServiceImpl<WeChatMessageMapper, BaseWeChatMessage> implements WeChatMessageService {

}
