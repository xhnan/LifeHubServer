package com.xhn.health.userprofiles.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.health.userprofiles.model.HealthyUserProfiles;

/**
 * 用户健康档案 服务接口
 *
 * @author xhn
 * @date 2026-03-13
 */
public interface HealthyUserProfilesService extends IService<HealthyUserProfiles> {

    /**
     * 根据用户ID查询健康档案
     *
     * @param userId 用户ID
     * @return 健康档案
     */
    HealthyUserProfiles getUserProfileByUserId(Long userId);
}
