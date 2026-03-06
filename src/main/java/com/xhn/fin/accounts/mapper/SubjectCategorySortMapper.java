package com.xhn.fin.accounts.mapper;

import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SubjectCategorySortMapper {

    List<Map<String, Object>> countExpensePaymentUsage(@Param("bookId") Long bookId,
                                                       @Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate);

    List<Map<String, Object>> countExpenseOccurrenceUsage(@Param("bookId") Long bookId,
                                                          @Param("startDate") LocalDateTime startDate,
                                                          @Param("endDate") LocalDateTime endDate);
}