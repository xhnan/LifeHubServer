package com.xhn.health.psychology.profiles.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.health.psychology.profiles.model.HealthPsyProfiles;

/**
 * 心理档案 服务接口
 *
 * @author xhn
 * @date 2026-03-16
 */
public interface HealthPsyProfilesService extends IService<HealthPsyProfiles> {

    /**
     * 根据用户ID查询心理档案
     *
     * @param userId 用户ID
     * @return 心理档案
     */
    HealthPsyProfiles getProfileByUserId(Long userId);
}
