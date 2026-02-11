package com.xhn.fin.bookmembers.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.fin.bookmembers.mapper.FinBookMembersMapper;
import com.xhn.fin.bookmembers.model.FinBookMembers;
import com.xhn.fin.bookmembers.service.FinBookMembersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhn.fin.books.service.FinBooksService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 账本成员服务实现类
 *
 * @author xhn
 * @date 2026-02-07
 */
@RequiredArgsConstructor
@Service
public class FinBookMembersServiceImpl extends ServiceImpl<FinBookMembersMapper, FinBookMembers> implements FinBookMembersService {

    private final FinBooksService finBooksService;

    @Override
    public boolean hasAccess(Long bookId, Long userId) {
        // 1. 检查用户是否是账本拥有者
        var book = finBooksService.getById(bookId);
        if (book != null && book.getOwnerId().equals(userId)) {
            return true;
        }

        // 2. 检查用户是否是账本成员
        LambdaQueryWrapper<FinBookMembers> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinBookMembers::getBookId, bookId)
               .eq(FinBookMembers::getUserId, userId);
        return this.count(wrapper) > 0;
    }
}