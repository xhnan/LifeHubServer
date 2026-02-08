package com.xhn.fin.books.service;

import com.xhn.fin.books.model.FinBooks;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 账簿信息 服务接口
 *
 * @author xhn
 * @since 2026-02-07
 */
public interface FinBooksService extends IService<FinBooks> {

    /**
     * 获取用户相关的账本列表
     * 包括用户创建的账本和用户加入的账本
     *
     * @param userId 用户ID
     * @return 账本列表
     */
    List<FinBooks> getBooksByUserId(Long userId);
}
