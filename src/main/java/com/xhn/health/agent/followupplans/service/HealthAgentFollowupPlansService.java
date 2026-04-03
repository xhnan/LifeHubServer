package com.xhn.health.agent.followupplans.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.health.agent.followupplans.model.HealthAgentFollowupPlans;

import java.util.List;

public interface HealthAgentFollowupPlansService extends IService<HealthAgentFollowupPlans> {
    List<HealthAgentFollowupPlans> getFollowupPlansByUserId(Long userId);

    List<HealthAgentFollowupPlans> getActiveFollowupPlansByUserId(Long userId);
}
