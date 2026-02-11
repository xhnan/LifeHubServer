package com.xhn.fin.entries.mapper;

import com.xhn.fin.entries.model.FinEntries;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
}