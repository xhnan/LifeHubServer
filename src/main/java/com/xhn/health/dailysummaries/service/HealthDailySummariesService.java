package com.xhn.health.dailysummaries.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.health.dailysummaries.model.HealthDailySummaries;

import java.time.LocalDate;
import java.util.List;

/**
 * 每日健康汇总 服务接口
 *
 * @author xhn
 * @date 2026-03-13
 */
public interface HealthDailySummariesService extends IService<HealthDailySummaries> {

    /**
     * 根据用户ID查询每日汇总列表
     *
     * @param userId 用户ID
     * @return 每日汇总列表
     */
    List<HealthDailySummaries> getSummariesByUserId(Long userId);

    /**
     * 根据用户ID和日期查询每日汇总
     *
     * @param userId 用户ID
     * @param recordDate 记录日期
     * @return 每日汇总
     */
    HealthDailySummaries getSummaryByUserIdAndDate(Long userId, LocalDate recordDate);

    /**
     * 根据用户ID和日期范围查询每日汇总
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每日汇总列表
     */
    List<HealthDailySummaries> getSummariesByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);
}
