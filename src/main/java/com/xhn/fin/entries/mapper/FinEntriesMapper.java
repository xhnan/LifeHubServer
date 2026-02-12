package com.xhn.fin.entries.mapper;

import com.xhn.fin.entries.model.FinEntries;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 财务分录 Mapper 接口
 *
 * @author xhn
 * @since 2026-02-04
 */
public interface FinEntriesMapper extends BaseMapper<FinEntries> {

    /**
     * 统计指定账本在本月收入科目的总额
     * 收入 = 收入科目(INCOME)的贷方金额 - 借方金额
     *
     * @param bookId 账本ID
     * @param monthStart 本月开始时间
     * @param monthEnd 本月结束时间
     * @return 本月收入总额
     */
    BigDecimal sumIncomeByMonth(@Param("bookId") Long bookId,
                                 @Param("monthStart") LocalDateTime monthStart,
                                 @Param("monthEnd") LocalDateTime monthEnd);

    /**
     * 统计指定账本在本月支出科目的总额
     * 支出 = 支出科目(EXPENSE)的借方金额 - 贷方金额
     *
     * @param bookId 账本ID
     * @param monthStart 本月开始时间
     * @param monthEnd 本月结束时间
     * @return 本月支出总额
     */
    BigDecimal sumExpenseByMonth(@Param("bookId") Long bookId,
                                  @Param("monthStart") LocalDateTime monthStart,
                                  @Param("monthEnd") LocalDateTime monthEnd);

    /**
     * 统计指定账本的资产类科目余额合计
     * 资产余额 = 初始余额 + 借方金额 - 贷方金额
     *
     * @param bookId 账本ID
     * @return 总资产
     */
    BigDecimal sumAssetBalance(@Param("bookId") Long bookId);

    /**
     * 统计指定账本的负债类科目余额合计
     * 负债余额 = 初始余额 + 贷方金额 - 借方金额
     *
     * @param bookId 账本ID
     * @return 总负债
     */
    BigDecimal sumLiabilityBalance(@Param("bookId") Long bookId);

    /**
     * 按科目分组统计指定账本的每个科目净变动额
     *
     * @param bookId 账本ID
     * @return 每个科目的 [account_id, net_amount] 列表
     */
    List<Map<String, Object>> sumBalanceChangeByAccount(@Param("bookId") Long bookId);

    /**
     * 按支出科目分组统计指定时间范围内的支出排行
     */
    List<Map<String, Object>> sumExpenseByCategory(@Param("bookId") Long bookId,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

    /**
     * 按收入科目分组统计指定时间范围内的收入排行
     */
    List<Map<String, Object>> sumIncomeByCategory(@Param("bookId") Long bookId,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);

    /**
     * 按标签分组统计指定时间范围内的支出
     */
    List<Map<String, Object>> sumExpenseByTag(@Param("bookId") Long bookId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    /**
     * 查询各资产科目余额明细
     */
    List<Map<String, Object>> listAssetAccountBalances(@Param("bookId") Long bookId);

    /**
     * 查询各负债科目余额明细
     */
    List<Map<String, Object>> listLiabilityAccountBalances(@Param("bookId") Long bookId);

}