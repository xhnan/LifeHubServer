package com.xhn.health.dailysummaries.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.health.dailysummaries.mapper.HealthDailySummariesMapper;
import com.xhn.health.dailysummaries.model.HealthDailySummaries;
import com.xhn.health.dailysummaries.service.HealthDailySummariesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 每日健康汇总 服务实现类
 *
 * @author xhn
 * @date 2026-03-13
 */
@Slf4j
@Service
public class HealthDailySummariesServiceImpl extends ServiceImpl<HealthDailySummariesMapper, HealthDailySummaries> implements HealthDailySummariesService {

    @Override
    public List<HealthDailySummaries> getSummariesByUserId(Long userId) {
        LambdaQueryWrapper<HealthDailySummaries> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthDailySummaries::getUserId, userId)
                .orderByDesc(HealthDailySummaries::getRecordDate);
        return this.list(wrapper);
    }

    @Override
    public HealthDailySummaries getSummaryByUserIdAndDate(Long userId, LocalDate recordDate) {
        LambdaQueryWrapper<HealthDailySummaries> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthDailySummaries::getUserId, userId)
                .eq(HealthDailySummaries::getRecordDate, recordDate);
        return this.getOne(wrapper);
    }

    @Override
    public List<HealthDailySummaries> getSummariesByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<HealthDailySummaries> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthDailySummaries::getUserId, userId)
                .ge(HealthDailySummaries::getRecordDate, startDate)
                .le(HealthDailySummaries::getRecordDate, endDate)
                .orderByDesc(HealthDailySummaries::getRecordDate);
        return this.list(wrapper);
    }
}
