package com.xhn.health.goals.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.health.goals.mapper.HealthGoalsMapper;
import com.xhn.health.goals.model.HealthGoals;
import com.xhn.health.goals.service.HealthGoalsService;
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
public class HealthGoalsServiceImpl extends ServiceImpl<HealthGoalsMapper, HealthGoals> implements HealthGoalsService {

    @Override
    public List<HealthGoals> getGoalsByUserId(Long userId) {
        LambdaQueryWrapper<HealthGoals> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthGoals::getUserId, userId)
                .orderByDesc(HealthGoals::getCreatedAt);
        return this.list(wrapper);
    }

    @Override
    public List<HealthGoals> getGoalsByUserIdAndStatus(Long userId, String status) {
        LambdaQueryWrapper<HealthGoals> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthGoals::getUserId, userId)
                .eq(HealthGoals::getStatus, status)
                .orderByDesc(HealthGoals::getCreatedAt);
        return this.list(wrapper);
    }

    @Override
    public List<HealthGoals> getGoalsByUserIdAndType(Long userId, String goalType) {
        LambdaQueryWrapper<HealthGoals> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthGoals::getUserId, userId)
                .eq(HealthGoals::getGoalType, goalType)
                .orderByDesc(HealthGoals::getCreatedAt);
        return this.list(wrapper);
    }
}
