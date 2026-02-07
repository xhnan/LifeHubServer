package com.xhn.fin.transactions.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.fin.transactions.model.FinTransactions;

/**
 * 财务交易记录 服务接口
 *
 * @author xhn
 * @since 2026-02-04
 */
public interface FinTransactionsService extends IService<FinTransactions> {

    /**
     * 根据账本ID和日期范围分页查询交易记录
     *
     * @param page 分页对象
     * @param bookId 账本ID
     * @param startDate 开始日期 (yyyy-MM-dd)，可选
     * @param endDate 结束日期 (yyyy-MM-dd)，可选
     * @return 分页结果
     */
    Page<FinTransactions> pageByBookIdAndDateRange(Page<FinTransactions> page, Long bookId, String startDate, String endDate);
}
