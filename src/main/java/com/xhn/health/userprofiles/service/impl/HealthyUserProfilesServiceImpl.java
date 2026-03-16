package com.xhn.health.userprofiles.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.health.userprofiles.mapper.HealthyUserProfilesMapper;
import com.xhn.health.userprofiles.model.HealthyUserProfiles;
import com.xhn.health.userprofiles.service.HealthyUserProfilesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户健康档案 服务实现类
 *
 * @author xhn
 * @date 2026-03-13
 */
@Slf4j
@Service
public class HealthyUserProfilesServiceImpl extends ServiceImpl<HealthyUserProfilesMapper, HealthyUserProfiles> implements HealthyUserProfilesService {

    @Override
    public HealthyUserProfiles getUserProfileByUserId(Long userId) {
        LambdaQueryWrapper<HealthyUserProfiles> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthyUserProfiles::getUserId, userId);
        return this.getOne(wrapper);
    }
}
