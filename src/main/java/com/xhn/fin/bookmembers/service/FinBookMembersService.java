package com.xhn.fin.bookmembers.service;

import com.xhn.fin.bookmembers.model.FinBookMembers;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 账本成员服务接口
 *
 * @author xhn
 * @since 2026-02-07
 */
/**
 * 账本成员服务接口
 *
 * @author xhn
 * @since 2026-02-07
 */
public interface FinBookMembersService extends IService<FinBookMembers> {

    /**
     * 检查用户是否有权限访问指定账本
     *
     * @param bookId 账本ID
     * @param userId 用户ID
     * @return true 如果用户是账本成员，否则 false
     */
    boolean hasAccess(Long bookId, Long userId);
}
