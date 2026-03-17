package com.xhn.health.psychology.assessments.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.health.psychology.assessments.mapper.HealthPsyAssessmentsMapper;
import com.xhn.health.psychology.assessments.model.HealthPsyAssessments;
import com.xhn.health.psychology.assessments.service.HealthPsyAssessmentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 心理评估 服务实现类
 *
 * @author xhn
 * @date 2026-03-16
 */
@Slf4j
@Service
public class HealthPsyAssessmentsServiceImpl extends ServiceImpl<HealthPsyAssessmentsMapper, HealthPsyAssessments> implements HealthPsyAssessmentsService {

    @Override
    public List<HealthPsyAssessments> getAssessmentsByUserId(Long userId) {
        LambdaQueryWrapper<HealthPsyAssessments> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthPsyAssessments::getUserId, userId)
                .orderByDesc(HealthPsyAssessments::getCreatedAt);
        return this.list(wrapper);
    }

    @Override
    public List<HealthPsyAssessments> getAssessmentsByUserIdAndScaleName(Long userId, String scaleName) {
        LambdaQueryWrapper<HealthPsyAssessments> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthPsyAssessments::getUserId, userId)
                .eq(HealthPsyAssessments::getScaleName, scaleName)
                .orderByDesc(HealthPsyAssessments::getCreatedAt);
        return this.list(wrapper);
    }

    @Override
    public HealthPsyAssessments getLatestAssessmentByUserId(Long userId) {
        LambdaQueryWrapper<HealthPsyAssessments> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthPsyAssessments::getUserId, userId)
                .orderByDesc(HealthPsyAssessments::getCreatedAt)
                .last("LIMIT 1");
        return this.getOne(wrapper);
    }
}
