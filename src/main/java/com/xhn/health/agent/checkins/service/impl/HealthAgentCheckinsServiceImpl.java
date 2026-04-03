package com.xhn.health.agent.checkins.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhn.health.agent.checkins.mapper.HealthAgentCheckinsMapper;
import com.xhn.health.agent.checkins.model.HealthAgentCheckins;
import com.xhn.health.agent.checkins.service.HealthAgentCheckinsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthAgentCheckinsServiceImpl extends ServiceImpl<HealthAgentCheckinsMapper, HealthAgentCheckins>
        implements HealthAgentCheckinsService {

    @Override
    public List<HealthAgentCheckins> getCheckinsByUserId(Long userId) {
        LambdaQueryWrapper<HealthAgentCheckins> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthAgentCheckins::getUserId, userId)
                .orderByDesc(HealthAgentCheckins::getCheckinDate)
                .orderByDesc(HealthAgentCheckins::getCreatedAt);
        return list(wrapper);
    }

    @Override
    public List<HealthAgentCheckins> getCheckinsByFollowupPlanId(Long followupPlanId) {
        LambdaQueryWrapper<HealthAgentCheckins> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthAgentCheckins::getFollowupPlanId, followupPlanId)
                .orderByDesc(HealthAgentCheckins::getCheckinDate)
                .orderByDesc(HealthAgentCheckins::getCreatedAt);
        return list(wrapper);
    }
}
