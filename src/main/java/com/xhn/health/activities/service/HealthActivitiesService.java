package com.xhn.health.activities.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.health.activities.model.HealthActivities;

import java.util.List;

/**
 * 健康活动 服务接口
 *
 * @author xhn
 * @date 2026-03-13
 */
public interface HealthActivitiesService extends IService<HealthActivities> {

    /**
     * 根据用户ID查询活动列表
     *
     * @param userId 用户ID
     * @return 活动列表
     */
    List<HealthActivities> getActivitiesByUserId(Long userId);

    /**
     * 根据用户ID和活动类型查询活动列表
     *
     * @param userId 用户ID
     * @param activityType 活动类型
     * @return 活动列表
     */
    List<HealthActivities> getActivitiesByUserIdAndType(Long userId, String activityType);
}
