package com.xhn.health.dietlogs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xhn.health.dietlogs.mapper.HealthyDietLogsMapper;
import com.xhn.health.dietlogs.model.HealthyDietLogs;
import com.xhn.health.dietlogs.service.HealthyDietLogsService;
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
public class HealthyDietLogsServiceImpl extends ServiceImpl<HealthyDietLogsMapper, HealthyDietLogs> implements HealthyDietLogsService {

    @Override
    public List<HealthyDietLogs> getDietLogsByUserId(Long userId) {
        LambdaQueryWrapper<HealthyDietLogs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthyDietLogs::getUserId, userId)
                .orderByDesc(HealthyDietLogs::getMealTime);
        return this.list(wrapper);
    }

    @Override
    public List<HealthyDietLogs> getDietLogsByUserIdAndMealType(Long userId, String mealType) {
        LambdaQueryWrapper<HealthyDietLogs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthyDietLogs::getUserId, userId)
                .eq(HealthyDietLogs::getMealType, mealType)
                .orderByDesc(HealthyDietLogs::getMealTime);
        return this.list(wrapper);
    }

    @Override
    public List<HealthyDietLogs> getDietLogsByUserIdAndDate(Long userId, LocalDate date) {
        LambdaQueryWrapper<HealthyDietLogs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthyDietLogs::getUserId, userId)
                .ge(HealthyDietLogs::getMealTime, date.atStartOfDay())
                .lt(HealthyDietLogs::getMealTime, date.plusDays(1).atStartOfDay())
                .orderByAsc(HealthyDietLogs::getMealTime);
        return this.list(wrapper);
    }
}
