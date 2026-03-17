package com.xhn.health.psychology.dailymoods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.health.psychology.dailymoods.mapper.HealthPsyDailyMoodsMapper;
import com.xhn.health.psychology.dailymoods.model.HealthPsyDailyMoods;
import com.xhn.health.psychology.dailymoods.service.HealthPsyDailyMoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 每日心情记录 服务实现类
 *
 * @author xhn
 * @date 2026-03-16
 */
@Slf4j
@Service
public class HealthPsyDailyMoodsServiceImpl extends ServiceImpl<HealthPsyDailyMoodsMapper, HealthPsyDailyMoods> implements HealthPsyDailyMoodsService {

    @Override
    public List<HealthPsyDailyMoods> getMoodsByUserId(Long userId) {
        LambdaQueryWrapper<HealthPsyDailyMoods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthPsyDailyMoods::getUserId, userId)
                .orderByDesc(HealthPsyDailyMoods::getRecordDate);
        return this.list(wrapper);
    }

    @Override
    public HealthPsyDailyMoods getMoodByUserIdAndDate(Long userId, LocalDate recordDate) {
        LambdaQueryWrapper<HealthPsyDailyMoods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthPsyDailyMoods::getUserId, userId)
                .eq(HealthPsyDailyMoods::getRecordDate, recordDate);
        return this.getOne(wrapper);
    }

    @Override
    public List<HealthPsyDailyMoods> getMoodsByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<HealthPsyDailyMoods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthPsyDailyMoods::getUserId, userId)
                .ge(HealthPsyDailyMoods::getRecordDate, startDate)
                .le(HealthPsyDailyMoods::getRecordDate, endDate)
                .orderByAsc(HealthPsyDailyMoods::getRecordDate);
        return this.list(wrapper);
    }

    @Override
    public HealthPsyDailyMoods getLatestMoodByUserId(Long userId) {
        LambdaQueryWrapper<HealthPsyDailyMoods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthPsyDailyMoods::getUserId, userId)
                .orderByDesc(HealthPsyDailyMoods::getRecordDate)
                .last("LIMIT 1");
        return this.getOne(wrapper);
    }
}
