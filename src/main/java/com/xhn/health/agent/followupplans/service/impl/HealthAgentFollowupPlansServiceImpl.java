package com.xhn.health.agent.followupplans.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhn.health.agent.followupplans.mapper.HealthAgentFollowupPlansMapper;
import com.xhn.health.agent.followupplans.model.HealthAgentFollowupPlans;
import com.xhn.health.agent.followupplans.service.HealthAgentFollowupPlansService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthAgentFollowupPlansServiceImpl extends ServiceImpl<HealthAgentFollowupPlansMapper, HealthAgentFollowupPlans>
        implements HealthAgentFollowupPlansService {

    @Override
    public List<HealthAgentFollowupPlans> getFollowupPlansByUserId(Long userId) {
        LambdaQueryWrapper<HealthAgentFollowupPlans> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthAgentFollowupPlans::getUserId, userId)
                .orderByDesc(HealthAgentFollowupPlans::getCreatedAt);
        return list(wrapper);
    }

    @Override
    public List<HealthAgentFollowupPlans> getActiveFollowupPlansByUserId(Long userId) {
        LambdaQueryWrapper<HealthAgentFollowupPlans> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthAgentFollowupPlans::getUserId, userId)
                .eq(HealthAgentFollowupPlans::getStatus, "active")
                .orderByDesc(HealthAgentFollowupPlans::getCreatedAt);
        return list(wrapper);
    }
}
