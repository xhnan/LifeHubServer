package com.xhn.health.agent.checkins.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.health.agent.checkins.model.HealthAgentCheckins;

import java.util.List;

public interface HealthAgentCheckinsService extends IService<HealthAgentCheckins> {
    List<HealthAgentCheckins> getCheckinsByUserId(Long userId);

    List<HealthAgentCheckins> getCheckinsByFollowupPlanId(Long followupPlanId);
}
