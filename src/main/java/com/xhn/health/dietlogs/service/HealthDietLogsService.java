package com.xhn.health.dietlogs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.health.dietlogs.model.HealthDietLogs;

import java.time.LocalDate;
import java.util.List;

/**
 * 饮食日志 服务接口
 *
 * @author xhn
 * @date 2026-03-13
 */
public interface HealthDietLogsService extends IService<HealthDietLogs> {

    /**
     * 根据用户ID查询饮食日志列表
     *
     * @param userId 用户ID
     * @return 饮食日志列表
     */
    List<HealthDietLogs> getDietLogsByUserId(Long userId);

    /**
     * 根据用户ID和用餐类型查询饮食日志列表
     *
     * @param userId 用户ID
     * @param mealType 用餐类型
     * @return 饮食日志列表
     */
    List<HealthDietLogs> getDietLogsByUserIdAndMealType(Long userId, String mealType);

    /**
     * 根据用户ID和日期查询饮食日志列表
     *
     * @param userId 用户ID
     * @param date 日期
     * @return 饮食日志列表
     */
    List<HealthDietLogs> getDietLogsByUserIdAndDate(Long userId, LocalDate date);
}
