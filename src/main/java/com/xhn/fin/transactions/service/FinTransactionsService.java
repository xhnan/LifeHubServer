package com.xhn.fin.transactions.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.fin.transactions.dto.MonthlyStatisticsDTO;
import com.xhn.fin.transactions.dto.TransactionEntryDTO;
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

    /**
     * 统一记账接口：创建交易及分录
     * 在一个事务中同时创建交易主表和分录表，并自动关联 transId
     *
     * @param dto 交易及分录请求 DTO
     * @param userId 当前用户 ID
     * @return 交易 ID
     * @throws IllegalArgumentException 如果借贷不平衡或分录为空
     */
    Long createTransactionWithEntries(TransactionEntryDTO dto, Long userId);

    /**
     * 获取本月收支统计
     *
     * @param bookId 账本ID
     * @return 本月统计数据（收入、支出、结余）
     */
    MonthlyStatisticsDTO getMonthlyStatistics(Long bookId);
}
