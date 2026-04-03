package com.xhn.health.agent.advicerecords.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xhn.health.agent.advicerecords.model.HealthAgentAdviceRecords;

import java.util.List;

public interface HealthAgentAdviceRecordsService extends IService<HealthAgentAdviceRecords> {
    List<HealthAgentAdviceRecords> getAdviceRecordsByUserId(Long userId);

    List<HealthAgentAdviceRecords> getAdviceRecordsByUserIdAndAgentType(Long userId, String agentType);

    List<HealthAgentAdviceRecords> getActiveAdviceRecordsByUserId(Long userId);
}
