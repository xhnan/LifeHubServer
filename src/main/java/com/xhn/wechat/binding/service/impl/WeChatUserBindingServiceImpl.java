package com.xhn.wechat.binding.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhn.wechat.binding.mapper.WeChatUserBindingMapper;
import com.xhn.wechat.binding.model.BaseWeChatUserBinding;
import com.xhn.wechat.binding.service.WeChatUserBindingService;
import org.springframework.stereotype.Service;

/**
 * 企业微信用户绑定 服务实现类
 * @author xhn
 * @date 2026-02-26
 */
@Service
public class WeChatUserBindingServiceImpl extends ServiceImpl<WeChatUserBindingMapper, BaseWeChatUserBinding> implements WeChatUserBindingService {

    @Override
    public BaseWeChatUserBinding getByUserIdAndAppId(Long userId, Long appId) {
        LambdaQueryWrapper<BaseWeChatUserBinding> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseWeChatUserBinding::getUserId, userId)
                .eq(BaseWeChatUserBinding::getAppId, appId)
                .eq(BaseWeChatUserBinding::getIsPrimary, 1);
        return getOne(wrapper);
    }

    @Override
    public BaseWeChatUserBinding getByWxUserIdAndAppId(String wxUserId, Long appId) {
        LambdaQueryWrapper<BaseWeChatUserBinding> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseWeChatUserBinding::getWxUserId, wxUserId)
                .eq(BaseWeChatUserBinding::getAppId, appId);
        return getOne(wrapper);
    }
}
