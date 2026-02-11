package com.xhn.fin.bookmembers.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.fin.bookmembers.mapper.FinBookMembersMapper;
import com.xhn.fin.bookmembers.model.FinBookMembers;
import com.xhn.fin.bookmembers.service.FinBookMembersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 账本成员服务实现类
 *
 * @author xhn
 * @date 2026-02-07
 */
@Service
public class FinBookMembersServiceImpl extends ServiceImpl<FinBookMembersMapper, FinBookMembers> implements FinBookMembersService {

    @Override
    public boolean hasAccess(Long bookId, Long userId) {
        LambdaQueryWrapper<FinBookMembers> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinBookMembers::getBookId, bookId)
               .eq(FinBookMembers::getUserId, userId);
        return this.count(wrapper) > 0;
    }
}