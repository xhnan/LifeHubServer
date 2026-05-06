package com.xhn.health.weightlogs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.health.weightlogs.mapper.HealthWeightLogsMapper;
import com.xhn.health.weightlogs.model.HealthWeightLogs;
import com.xhn.health.weightlogs.service.HealthWeightLogsService;
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
public class HealthWeightLogsServiceImpl extends ServiceImpl<HealthWeightLogsMapper, HealthWeightLogs> implements HealthWeightLogsService {

    @Override
    public List<HealthWeightLogs> getWeightLogsByUserId(Long userId) {
        LambdaQueryWrapper<HealthWeightLogs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthWeightLogs::getUserId, userId)
                .orderByDesc(HealthWeightLogs::getRecordDate);
        return this.list(wrapper);
    }

    @Override
    public HealthWeightLogs getWeightLogByUserIdAndDate(Long userId, LocalDate recordDate) {
        LambdaQueryWrapper<HealthWeightLogs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthWeightLogs::getUserId, userId)
                .eq(HealthWeightLogs::getRecordDate, recordDate);
        return this.getOne(wrapper);
    }

    @Override
    public List<HealthWeightLogs> getWeightLogsByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<HealthWeightLogs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthWeightLogs::getUserId, userId)
                .ge(HealthWeightLogs::getRecordDate, startDate)
                .le(HealthWeightLogs::getRecordDate, endDate)
                .orderByAsc(HealthWeightLogs::getRecordDate);
        return this.list(wrapper);
    }

    @Override
    public HealthWeightLogs getLatestWeightLogByUserId(Long userId) {
        LambdaQueryWrapper<HealthWeightLogs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthWeightLogs::getUserId, userId)
                .orderByDesc(HealthWeightLogs::getRecordDate)
                .last("LIMIT 1");
        return this.getOne(wrapper);
    }
}
