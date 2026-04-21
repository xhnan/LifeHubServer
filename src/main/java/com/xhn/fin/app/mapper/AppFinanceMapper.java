package com.xhn.fin.app.mapper;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AppFinanceMapper {

    BigDecimal sumBalanceByType(@Param("bookId") Long bookId,
                                @Param("accountType") String accountType,
                                @Param("asOf") LocalDateTime asOf);

    List<Map<String, Object>> listAccountBalances(@Param("bookId") Long bookId);

    List<Map<String, Object>> selectDailyIncomeExpense(@Param("bookId") Long bookId,
                                                       @Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate);

    List<Map<String, Object>> selectCategoryTotals(@Param("bookId") Long bookId,
                                                   @Param("accountType") String accountType,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);

    long countRecords(@Param("bookId") Long bookId,
                      @Param("startDate") LocalDateTime startDate,
                      @Param("endDate") LocalDateTime endDate,
                      @Param("dateStart") LocalDateTime dateStart,
                      @Param("dateEnd") LocalDateTime dateEnd,
                      @Param("accountId") Long accountId,
                      @Param("category") String category,
                      @Param("minAmount") BigDecimal minAmount,
                      @Param("maxAmount") BigDecimal maxAmount,
                      @Param("keyword") String keyword);

    List<Map<String, Object>> selectRecords(@Param("bookId") Long bookId,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate,
                                            @Param("dateStart") LocalDateTime dateStart,
                                            @Param("dateEnd") LocalDateTime dateEnd,
                                            @Param("accountId") Long accountId,
                                            @Param("category") String category,
                                            @Param("minAmount") BigDecimal minAmount,
                                            @Param("maxAmount") BigDecimal maxAmount,
                                            @Param("keyword") String keyword,
                                            @Param("offset") long offset,
                                            @Param("size") long size);

    Map<String, Object> selectMonthlySummary(@Param("bookId") Long bookId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    List<Map<String, Object>> selectTopRecords(@Param("bookId") Long bookId,
                                               @Param("type") String type,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate,
                                               @Param("size") int size);
}
