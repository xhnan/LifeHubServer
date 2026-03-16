package com.xhn.health.dailysummaries.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.health.dailysummaries.mapper.HealthyDailySummariesMapper;
import com.xhn.health.dailysummaries.model.HealthyDailySummaries;
import com.xhn.health.dailysummaries.service.HealthyDailySummariesService;
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
public class HealthyDailySummariesServiceImpl extends ServiceImpl<HealthyDailySummariesMapper, HealthyDailySummaries> implements HealthyDailySummariesService {

    @Override
    public List<HealthyDailySummaries> getSummariesByUserId(Long userId) {
        LambdaQueryWrapper<HealthyDailySummaries> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthyDailySummaries::getUserId, userId)
                .orderByDesc(HealthyDailySummaries::getRecordDate);
        return this.list(wrapper);
    }

    @Override
    public HealthyDailySummaries getSummaryByUserIdAndDate(Long userId, LocalDate recordDate) {
        LambdaQueryWrapper<HealthyDailySummaries> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthyDailySummaries::getUserId, userId)
                .eq(HealthyDailySummaries::getRecordDate, recordDate);
        return this.getOne(wrapper);
    }

    @Override
    public List<HealthyDailySummaries> getSummariesByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<HealthyDailySummaries> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthyDailySummaries::getUserId, userId)
                .ge(HealthyDailySummaries::getRecordDate, startDate)
                .le(HealthyDailySummaries::getRecordDate, endDate)
                .orderByDesc(HealthyDailySummaries::getRecordDate);
        return this.list(wrapper);
    }
}
