package com.xhn.health.psychology.assessments.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.health.psychology.assessments.model.HealthPsyAssessments;

import java.util.List;

/**
 * 心理评估 服务接口
 *
 * @author xhn
 * @date 2026-03-16
 */
public interface HealthPsyAssessmentsService extends IService<HealthPsyAssessments> {

    /**
     * 根据用户ID查询评估记录列表
     *
     * @param userId 用户ID
     * @return 评估记录列表
     */
    List<HealthPsyAssessments> getAssessmentsByUserId(Long userId);

    /**
     * 根据用户ID和量表名称查询评估记录列表
     *
     * @param userId 用户ID
     * @param scaleName 量表名称
     * @return 评估记录列表
     */
    List<HealthPsyAssessments> getAssessmentsByUserIdAndScaleName(Long userId, String scaleName);

    /**
     * 获取用户最新的评估记录
     *
     * @param userId 用户ID
     * @return 最新的评估记录
     */
    HealthPsyAssessments getLatestAssessmentByUserId(Long userId);
}
