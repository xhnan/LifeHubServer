package com.xhn.health.userprofiles.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.health.userprofiles.model.HealthUserProfiles;

/**
 * 用户健康档案 服务接口
 *
 * @author xhn
 * @date 2026-03-13
 */
public interface HealthUserProfilesService extends IService<HealthUserProfiles> {

    /**
     * 根据用户ID查询健康档案
     *
     * @param userId 用户ID
     * @return 健康档案
     */
    HealthUserProfiles getUserProfileByUserId(Long userId);
}
