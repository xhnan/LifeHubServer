package com.xhn.health.agent.advicerecords.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhn.health.agent.advicerecords.mapper.HealthAgentAdviceRecordsMapper;
import com.xhn.health.agent.advicerecords.model.HealthAgentAdviceRecords;
import com.xhn.health.agent.advicerecords.service.HealthAgentAdviceRecordsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthAgentAdviceRecordsServiceImpl extends ServiceImpl<HealthAgentAdviceRecordsMapper, HealthAgentAdviceRecords>
        implements HealthAgentAdviceRecordsService {

    @Override
    public List<HealthAgentAdviceRecords> getAdviceRecordsByUserId(Long userId) {
        LambdaQueryWrapper<HealthAgentAdviceRecords> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthAgentAdviceRecords::getUserId, userId)
                .orderByDesc(HealthAgentAdviceRecords::getCreatedAt);
        return list(wrapper);
    }

    @Override
    public List<HealthAgentAdviceRecords> getAdviceRecordsByUserIdAndAgentType(Long userId, String agentType) {
        LambdaQueryWrapper<HealthAgentAdviceRecords> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthAgentAdviceRecords::getUserId, userId)
                .eq(HealthAgentAdviceRecords::getAgentType, agentType)
                .orderByDesc(HealthAgentAdviceRecords::getCreatedAt);
        return list(wrapper);
    }

    @Override
    public List<HealthAgentAdviceRecords> getActiveAdviceRecordsByUserId(Long userId) {
        LambdaQueryWrapper<HealthAgentAdviceRecords> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthAgentAdviceRecords::getUserId, userId)
                .eq(HealthAgentAdviceRecords::getStatus, "active")
                .orderByDesc(HealthAgentAdviceRecords::getCreatedAt);
        return list(wrapper);
    }
}
