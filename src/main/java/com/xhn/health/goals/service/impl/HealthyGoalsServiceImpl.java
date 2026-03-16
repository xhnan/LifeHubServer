package com.xhn.health.goals.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.health.goals.mapper.HealthyGoalsMapper;
import com.xhn.health.goals.model.HealthyGoals;
import com.xhn.health.goals.service.HealthyGoalsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 健康目标 服务实现类
 *
 * @author xhn
 * @date 2026-03-13
 */
@Slf4j
@Service
public class HealthyGoalsServiceImpl extends ServiceImpl<HealthyGoalsMapper, HealthyGoals> implements HealthyGoalsService {

    @Override
    public List<HealthyGoals> getGoalsByUserId(Long userId) {
        LambdaQueryWrapper<HealthyGoals> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthyGoals::getUserId, userId)
                .orderByDesc(HealthyGoals::getCreatedAt);
        return this.list(wrapper);
    }

    @Override
    public List<HealthyGoals> getGoalsByUserIdAndStatus(Long userId, String status) {
        LambdaQueryWrapper<HealthyGoals> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthyGoals::getUserId, userId)
                .eq(HealthyGoals::getStatus, status)
                .orderByDesc(HealthyGoals::getCreatedAt);
        return this.list(wrapper);
    }

    @Override
    public List<HealthyGoals> getGoalsByUserIdAndType(Long userId, String goalType) {
        LambdaQueryWrapper<HealthyGoals> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthyGoals::getUserId, userId)
                .eq(HealthyGoals::getGoalType, goalType)
                .orderByDesc(HealthyGoals::getCreatedAt);
        return this.list(wrapper);
    }
}
