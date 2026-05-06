package com.xhn.health.activities.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.health.activities.mapper.HealthActivitiesMapper;
import com.xhn.health.activities.model.HealthActivities;
import com.xhn.health.activities.service.HealthActivitiesService;
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
public class HealthActivitiesServiceImpl extends ServiceImpl<HealthActivitiesMapper, HealthActivities> implements HealthActivitiesService {

    @Override
    public List<HealthActivities> getActivitiesByUserId(Long userId) {
        LambdaQueryWrapper<HealthActivities> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthActivities::getUserId, userId)
                .orderByDesc(HealthActivities::getStartTime);
        return this.list(wrapper);
    }

    @Override
    public List<HealthActivities> getActivitiesByUserIdAndType(Long userId, String activityType) {
        LambdaQueryWrapper<HealthActivities> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthActivities::getUserId, userId)
                .eq(HealthActivities::getActivityType, activityType)
                .orderByDesc(HealthActivities::getStartTime);
        return this.list(wrapper);
    }
}
