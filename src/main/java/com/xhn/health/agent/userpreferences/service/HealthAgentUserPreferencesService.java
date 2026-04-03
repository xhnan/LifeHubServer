package com.xhn.health.agent.userpreferences.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.health.agent.userpreferences.model.HealthAgentUserPreferences;

public interface HealthAgentUserPreferencesService extends IService<HealthAgentUserPreferences> {
    HealthAgentUserPreferences getByUserId(Long userId);
}
