package com.xhn.wechat.binding.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xhn.wechat.binding.model.BaseWeChatUserBinding;
import org.apache.ibatis.annotations.Mapper;

/**
 * 企业微信用户绑定 Mapper
 * @author xhn
 * @date 2026-02-26
 */
@Mapper
public interface WeChatUserBindingMapper extends BaseMapper<BaseWeChatUserBinding> {
}
