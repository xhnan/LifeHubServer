package com.xhn.health.risk.flags.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.health.risk.flags.model.HealthRiskFlags;

import java.util.List;

public interface HealthRiskFlagsService extends IService<HealthRiskFlags> {
    List<HealthRiskFlags> getRiskFlagsByUserId(Long userId);
}
