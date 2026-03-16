package com.xhn.health.weightlogs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.health.weightlogs.mapper.HealthyWeightLogsMapper;
import com.xhn.health.weightlogs.model.HealthyWeightLogs;
import com.xhn.health.weightlogs.service.HealthyWeightLogsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 体重记录 服务实现类
 *
 * @author xhn
 * @date 2026-03-13
 */
@Slf4j
@Service
public class HealthyWeightLogsServiceImpl extends ServiceImpl<HealthyWeightLogsMapper, HealthyWeightLogs> implements HealthyWeightLogsService {

    @Override
    public List<HealthyWeightLogs> getWeightLogsByUserId(Long userId) {
        LambdaQueryWrapper<HealthyWeightLogs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthyWeightLogs::getUserId, userId)
                .orderByDesc(HealthyWeightLogs::getRecordDate);
        return this.list(wrapper);
    }

    @Override
    public HealthyWeightLogs getWeightLogByUserIdAndDate(Long userId, LocalDate recordDate) {
        LambdaQueryWrapper<HealthyWeightLogs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthyWeightLogs::getUserId, userId)
                .eq(HealthyWeightLogs::getRecordDate, recordDate);
        return this.getOne(wrapper);
    }

    @Override
    public List<HealthyWeightLogs> getWeightLogsByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<HealthyWeightLogs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthyWeightLogs::getUserId, userId)
                .ge(HealthyWeightLogs::getRecordDate, startDate)
                .le(HealthyWeightLogs::getRecordDate, endDate)
                .orderByAsc(HealthyWeightLogs::getRecordDate);
        return this.list(wrapper);
    }

    @Override
    public HealthyWeightLogs getLatestWeightLogByUserId(Long userId) {
        LambdaQueryWrapper<HealthyWeightLogs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthyWeightLogs::getUserId, userId)
                .orderByDesc(HealthyWeightLogs::getRecordDate)
                .last("LIMIT 1");
        return this.getOne(wrapper);
    }
}
