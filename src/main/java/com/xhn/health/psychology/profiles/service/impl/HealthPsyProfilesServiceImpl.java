package com.xhn.health.psychology.profiles.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.health.psychology.profiles.mapper.HealthPsyProfilesMapper;
import com.xhn.health.psychology.profiles.model.HealthPsyProfiles;
import com.xhn.health.psychology.profiles.service.HealthPsyProfilesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 心理档案 服务实现类
 *
 * @author xhn
 * @date 2026-03-16
 */
@Slf4j
@Service
public class HealthPsyProfilesServiceImpl extends ServiceImpl<HealthPsyProfilesMapper, HealthPsyProfiles> implements HealthPsyProfilesService {

    @Override
    public HealthPsyProfiles getProfileByUserId(Long userId) {
        LambdaQueryWrapper<HealthPsyProfiles> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthPsyProfiles::getUserId, userId);
        return this.getOne(wrapper);
    }
}
