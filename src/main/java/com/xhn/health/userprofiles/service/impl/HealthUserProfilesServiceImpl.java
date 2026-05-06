package com.xhn.health.userprofiles.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.health.userprofiles.mapper.HealthUserProfilesMapper;
import com.xhn.health.userprofiles.model.HealthUserProfiles;
import com.xhn.health.userprofiles.service.HealthUserProfilesService;
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
public class HealthUserProfilesServiceImpl extends ServiceImpl<HealthUserProfilesMapper, HealthUserProfiles> implements HealthUserProfilesService {

    @Override
    public HealthUserProfiles getUserProfileByUserId(Long userId) {
        LambdaQueryWrapper<HealthUserProfiles> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthUserProfiles::getUserId, userId);
        return this.getOne(wrapper);
    }
}
