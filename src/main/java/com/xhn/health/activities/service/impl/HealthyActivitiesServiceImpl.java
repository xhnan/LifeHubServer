package com.xhn.health.activities.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.health.activities.mapper.HealthyActivitiesMapper;
import com.xhn.health.activities.model.HealthyActivities;
import com.xhn.health.activities.service.HealthyActivitiesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 健康活动 服务实现类
 *
 * @author xhn
 * @date 2026-03-13
 */
@Slf4j
@Service
public class HealthyActivitiesServiceImpl extends ServiceImpl<HealthyActivitiesMapper, HealthyActivities> implements HealthyActivitiesService {

    @Override
    public List<HealthyActivities> getActivitiesByUserId(Long userId) {
        LambdaQueryWrapper<HealthyActivities> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthyActivities::getUserId, userId)
                .orderByDesc(HealthyActivities::getStartTime);
        return this.list(wrapper);
    }

    @Override
    public List<HealthyActivities> getActivitiesByUserIdAndType(Long userId, String activityType) {
        LambdaQueryWrapper<HealthyActivities> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthyActivities::getUserId, userId)
                .eq(HealthyActivities::getActivityType, activityType)
                .orderByDesc(HealthyActivities::getStartTime);
        return this.list(wrapper);
    }
}
