package com.xhn.fin.transactions.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.fin.transactions.dto.AccountBalanceDTO;
import com.xhn.fin.transactions.dto.CategoryRankDTO;
import com.xhn.fin.transactions.dto.MonthlyStatisticsDTO;
import com.xhn.fin.transactions.dto.TagStatisticsDTO;
import com.xhn.fin.transactions.dto.TransactionDetailDTO;
import com.xhn.fin.transactions.dto.TransactionEntryDTO;
import com.xhn.fin.transactions.dto.YearlyTrendDTO;
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

    /**
     * 获取指定月份收支统计
     *
     * @param bookId 账本ID
     * @param year   年份
     * @param month  月份 (1-12)
     * @return 月度统计数据（收入、支出、结余）
     */
    MonthlyStatisticsDTO getMonthlyStatistics(Long bookId, int year, int month);

    /**
     * 查询交易明细（流水账视图），支持时间范围筛选
     *
     * @param bookId    账本ID
     * @param startDate 开始日期 (yyyy-MM-dd)，可选
     * @param endDate   结束日期 (yyyy-MM-dd)，可选
     * @param pageNum   页码
     * @param pageSize  每页数量
     * @return 流水账视图
     */
    TransactionDetailDTO getTransactionDetails(Long bookId, String startDate, String endDate, int pageNum, int pageSize);

    /**
     * 获取年度收支趋势（按月统计）
     *
     * @param bookId 账本ID
     * @param year   年份
     * @return 12个月的收入/支出/结余趋势
     */
    YearlyTrendDTO getYearlyTrend(Long bookId, int year);

    /**
     * 获取分类排行（支出或收入按科目分类）
     *
     * @param bookId 账本ID
     * @param type   EXPENSE 或 INCOME
     * @param year   年份
     * @param month  月份 (1-12)
     * @return 分类排行
     */
    CategoryRankDTO getCategoryRank(Long bookId, String type, int year, int month);

    /**
     * 获取标签统计（按标签分组的支出统计）
     *
     * @param bookId 账本ID
     * @param year   年份
     * @param month  月份 (1-12)
     * @return 标签统计
     */
    TagStatisticsDTO getTagStatistics(Long bookId, int year, int month);

    /**
     * 获取资产/负债各科目余额明细
     *
     * @param bookId      账本ID
     * @param accountType ASSET 或 LIABILITY
     * @return 科目余额明细
     */
    AccountBalanceDTO getAccountBalances(Long bookId, String accountType);

}
