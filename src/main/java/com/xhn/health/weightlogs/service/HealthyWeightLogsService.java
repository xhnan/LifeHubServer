package com.xhn.health.weightlogs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.health.weightlogs.model.HealthyWeightLogs;

import java.time.LocalDate;
import java.util.List;

/**
 * 体重记录 服务接口
 *
 * @author xhn
 * @date 2026-03-13
 */
public interface HealthyWeightLogsService extends IService<HealthyWeightLogs> {

    /**
     * 根据用户ID查询体重记录列表
     *
     * @param userId 用户ID
     * @return 体重记录列表
     */
    List<HealthyWeightLogs> getWeightLogsByUserId(Long userId);

    /**
     * 根据用户ID和日期查询体重记录
     *
     * @param userId 用户ID
     * @param recordDate 记录日期
     * @return 体重记录
     */
    HealthyWeightLogs getWeightLogByUserIdAndDate(Long userId, LocalDate recordDate);

    /**
     * 根据用户ID和日期范围查询体重记录列表
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 体重记录列表
     */
    List<HealthyWeightLogs> getWeightLogsByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取用户最新的体重记录
     *
     * @param userId 用户ID
     * @return 最新的体重记录
     */
    HealthyWeightLogs getLatestWeightLogByUserId(Long userId);
}
