package com.xhn.wechat.binding.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.wechat.binding.model.BaseWeChatUserBinding;

/**
 * 企业微信用户绑定 服务接口
 * @author xhn
 * @date 2026-02-26
 */
public interface WeChatUserBindingService extends IService<BaseWeChatUserBinding> {

    /**
     * 根据系统用户ID和企业微信应用ID获取绑定关系
     * @param userId 系统用户ID
     * @param appId 应用ID
     * @return 绑定关系
     */
    BaseWeChatUserBinding getByUserIdAndAppId(Long userId, Long appId);

    /**
     * 根据企业微信UserID和应用ID获取绑定关系
     * @param wxUserId 企业微信UserID
     * @param appId 应用ID
     * @return 绑定关系
     */
    BaseWeChatUserBinding getByWxUserIdAndAppId(String wxUserId, Long appId);
}
