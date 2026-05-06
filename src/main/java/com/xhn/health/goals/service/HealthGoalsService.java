package com.xhn.health.goals.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.health.goals.model.HealthGoals;

import java.util.List;

/**
 * 健康目标 服务接口
 *
 * @author xhn
 * @date 2026-03-13
 */
public interface HealthGoalsService extends IService<HealthGoals> {

    /**
     * 根据用户ID查询健康目标列表
     *
     * @param userId 用户ID
     * @return 健康目标列表
     */
    List<HealthGoals> getGoalsByUserId(Long userId);

    /**
     * 根据用户ID和状态查询健康目标列表
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 健康目标列表
     */
    List<HealthGoals> getGoalsByUserIdAndStatus(Long userId, String status);

    /**
     * 根据用户ID和目标类型查询健康目标列表
     *
     * @param userId 用户ID
     * @param goalType 目标类型
     * @return 健康目标列表
     */
    List<HealthGoals> getGoalsByUserIdAndType(Long userId, String goalType);
}
