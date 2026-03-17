package com.xhn.health.psychology.dailymoods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.health.psychology.dailymoods.model.HealthPsyDailyMoods;

import java.time.LocalDate;
import java.util.List;

/**
 * 每日心情记录 服务接口
 *
 * @author xhn
 * @date 2026-03-16
 */
public interface HealthPsyDailyMoodsService extends IService<HealthPsyDailyMoods> {

    /**
     * 根据用户ID查询心情记录列表
     *
     * @param userId 用户ID
     * @return 心情记录列表
     */
    List<HealthPsyDailyMoods> getMoodsByUserId(Long userId);

    /**
     * 根据用户ID和日期查询心情记录
     *
     * @param userId 用户ID
     * @param recordDate 记录日期
     * @return 心情记录
     */
    HealthPsyDailyMoods getMoodByUserIdAndDate(Long userId, LocalDate recordDate);

    /**
     * 根据用户ID和日期范围查询心情记录列表
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 心情记录列表
     */
    List<HealthPsyDailyMoods> getMoodsByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取用户最新的心情记录
     *
     * @param userId 用户ID
     * @return 最新的心情记录
     */
    HealthPsyDailyMoods getLatestMoodByUserId(Long userId);
}
