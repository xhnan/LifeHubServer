package com.xhn.health.risk.flags.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhn.health.risk.flags.mapper.HealthRiskFlagsMapper;
import com.xhn.health.risk.flags.model.HealthRiskFlags;
import com.xhn.health.risk.flags.service.HealthRiskFlagsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthRiskFlagsServiceImpl extends ServiceImpl<HealthRiskFlagsMapper, HealthRiskFlags> implements HealthRiskFlagsService {
    @Override
    public List<HealthRiskFlags> getRiskFlagsByUserId(Long userId) {
        LambdaQueryWrapper<HealthRiskFlags> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRiskFlags::getUserId, userId)
                .orderByDesc(HealthRiskFlags::getCreatedAt);
        return list(wrapper);
    }
}
