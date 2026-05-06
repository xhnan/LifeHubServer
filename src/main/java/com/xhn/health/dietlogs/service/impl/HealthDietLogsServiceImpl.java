package com.xhn.health.dietlogs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.health.dietlogs.mapper.HealthDietLogsMapper;
import com.xhn.health.dietlogs.model.HealthDietLogs;
import com.xhn.health.dietlogs.service.HealthDietLogsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 饮食日志 服务实现类
 *
 * @author xhn
 * @date 2026-03-13
 */
@Slf4j
@Service
public class HealthDietLogsServiceImpl extends ServiceImpl<HealthDietLogsMapper, HealthDietLogs> implements HealthDietLogsService {

    @Override
    public List<HealthDietLogs> getDietLogsByUserId(Long userId) {
        LambdaQueryWrapper<HealthDietLogs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthDietLogs::getUserId, userId)
                .orderByDesc(HealthDietLogs::getMealTime);
        return this.list(wrapper);
    }

    @Override
    public List<HealthDietLogs> getDietLogsByUserIdAndMealType(Long userId, String mealType) {
        LambdaQueryWrapper<HealthDietLogs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthDietLogs::getUserId, userId)
                .eq(HealthDietLogs::getMealType, mealType)
                .orderByDesc(HealthDietLogs::getMealTime);
        return this.list(wrapper);
    }

    @Override
    public List<HealthDietLogs> getDietLogsByUserIdAndDate(Long userId, LocalDate date) {
        LambdaQueryWrapper<HealthDietLogs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthDietLogs::getUserId, userId)
                .ge(HealthDietLogs::getMealTime, date.atStartOfDay())
                .lt(HealthDietLogs::getMealTime, date.plusDays(1).atStartOfDay())
                .orderByAsc(HealthDietLogs::getMealTime);
        return this.list(wrapper);
    }
}
