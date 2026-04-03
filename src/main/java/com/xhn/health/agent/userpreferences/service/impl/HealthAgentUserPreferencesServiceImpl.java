package com.xhn.health.agent.userpreferences.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhn.health.agent.userpreferences.mapper.HealthAgentUserPreferencesMapper;
import com.xhn.health.agent.userpreferences.model.HealthAgentUserPreferences;
import com.xhn.health.agent.userpreferences.service.HealthAgentUserPreferencesService;
import org.springframework.stereotype.Service;

@Service
public class HealthAgentUserPreferencesServiceImpl extends ServiceImpl<HealthAgentUserPreferencesMapper, HealthAgentUserPreferences>
        implements HealthAgentUserPreferencesService {

    @Override
    public HealthAgentUserPreferences getByUserId(Long userId) {
        LambdaQueryWrapper<HealthAgentUserPreferences> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthAgentUserPreferences::getUserId, userId).last("LIMIT 1");
        return getOne(wrapper);
    }
}
