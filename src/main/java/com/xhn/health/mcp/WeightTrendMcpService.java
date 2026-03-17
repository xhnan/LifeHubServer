package com.xhn.health.mcp;

import com.xhn.health.activities.service.HealthyActivitiesService;
import com.xhn.health.dailysummaries.service.HealthyDailySummariesService;
import com.xhn.health.dietlogs.service.HealthyDietLogsService;
import com.xhn.health.weightlogs.model.HealthyWeightLogs;
import com.xhn.health.weightlogs.service.HealthyWeightLogsService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class WeightTrendMcpService {

    private final HealthyWeightLogsService weightLogsService;
    private final HealthyDietLogsService dietLogsService;
    private final HealthyActivitiesService activitiesService;
    private final HealthyDailySummariesService dailySummariesService;
    private final HealthMcpSupport support;

    public WeightTrendMcpService(
            HealthyWeightLogsService weightLogsService,
            HealthyDietLogsService dietLogsService,
            HealthyActivitiesService activitiesService,
            HealthyDailySummariesService dailySummariesService,
            HealthMcpSupport support) {
        this.weightLogsService = weightLogsService;
        this.dietLogsService = dietLogsService;
        this.activitiesService = activitiesService;
        this.dailySummariesService = dailySummariesService;
        this.support = support;
    }

    @Tool(name = "get_weight_logs", description = "读取体重记录")
    public List<Map<String, Object>> getWeightLogs(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "开始日期 YYYY-MM-DD") String startDate,
            @ToolParam(description = "结束日期 YYYY-MM-DD") String endDate) {
        return support.toList(weightLogsService.getWeightLogsByUserIdAndDateRange(
                userId, support.parseDate(startDate), support.parseDate(endDate)));
    }

    @Tool(name = "get_weight_trend_summary", description = "输出体重趋势、均值、波动幅度、变化方向")
    public Map<String, Object> getWeightTrendSummary(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "最近天数") Integer days) {
        LocalDate[] range = support.dateRange(days == null ? 14 : days);
        List<HealthyWeightLogs> logs = weightLogsService.getWeightLogsByUserIdAndDateRange(userId, range[0], range[1]);
        if (logs.isEmpty()) {
            return Map.of("count", 0);
        }

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal min = support.safe(logs.get(0).getWeightKg());
        BigDecimal max = support.safe(logs.get(0).getWeightKg());
        for (HealthyWeightLogs log : logs) {
            BigDecimal weight = support.safe(log.getWeightKg());
            total = total.add(weight);
            if (weight.compareTo(min) < 0) {
                min = weight;
            }
            if (weight.compareTo(max) > 0) {
                max = weight;
            }
        }

        BigDecimal first = support.safe(logs.get(0).getWeightKg());
        BigDecimal last = support.safe(logs.get(logs.size() - 1).getWeightKg());
        BigDecimal delta = last.subtract(first);
        return Map.of(
                "count", logs.size(),
                "startDate", range[0],
                "endDate", range[1],
                "averageWeightKg", support.round(total.divide(BigDecimal.valueOf(logs.size()), 2, BigDecimal.ROUND_HALF_UP)),
                "minWeightKg", support.round(min),
                "maxWeightKg", support.round(max),
                "changeKg", support.round(delta),
                "direction", delta.compareTo(BigDecimal.ZERO) > 0 ? "up" : delta.compareTo(BigDecimal.ZERO) < 0 ? "down" : "stable"
        );
    }

    @Tool(name = "get_bmi_bodyfat_summary", description = "输出 BMI 和体脂变化情况")
    public Map<String, Object> getBmiBodyfatSummary(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "最近天数") Integer days) {
        LocalDate[] range = support.dateRange(days == null ? 14 : days);
        List<HealthyWeightLogs> logs = weightLogsService.getWeightLogsByUserIdAndDateRange(userId, range[0], range[1]);
        if (logs.isEmpty()) {
            return Map.of("count", 0);
        }
        HealthyWeightLogs first = logs.get(0);
        HealthyWeightLogs last = logs.get(logs.size() - 1);
        return Map.of(
                "count", logs.size(),
                "startBmi", first.getBmi() == null ? null : support.round(first.getBmi()),
                "latestBmi", last.getBmi() == null ? null : support.round(last.getBmi()),
                "startBodyFatPercentage", first.getBodyFatPercentage() == null ? null : support.round(first.getBodyFatPercentage()),
                "latestBodyFatPercentage", last.getBodyFatPercentage() == null ? null : support.round(last.getBodyFatPercentage())
        );
    }

    @Tool(name = "get_weight_change_factors", description = "结合饮食和活动给出体重变化可能因素")
    public Map<String, Object> getWeightChangeFactors(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "最近天数") Integer days) {
        LocalDate[] range = support.dateRange(days == null ? 14 : days);

        BigDecimal intake = dietLogsService.getDietLogsByUserId(userId).stream()
                .filter(log -> !log.getMealTime().toLocalDate().isBefore(range[0]) && !log.getMealTime().toLocalDate().isAfter(range[1]))
                .map(log -> support.safe(log.getTotalCalories()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal workoutBurned = activitiesService.getActivitiesByUserId(userId).stream()
                .filter(log -> !log.getStartTime().toLocalDate().isBefore(range[0]) && !log.getStartTime().toLocalDate().isAfter(range[1]))
                .map(log -> support.safe(log.getCaloriesBurned()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal dailyActiveBurned = dailySummariesService.getSummariesByUserIdAndDateRange(userId, range[0], range[1]).stream()
                .map(log -> support.safe(log.getActiveCaloriesKcal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Map.of(
                "dietCalories", support.round(intake),
                "workoutCaloriesBurned", support.round(workoutBurned),
                "dailyActiveCalories", support.round(dailyActiveBurned),
                "observations", List.of(
                        intake.compareTo(workoutBurned.add(dailyActiveBurned)) > 0 ? "摄入高于活动消耗，可能推动体重上升" : "活动消耗与摄入相对接近",
                        "第一版未接入情绪数据，影响因素分析暂不包含情绪维度"
                )
        );
    }

    @Tool(name = "save_weight_analysis", description = "保存体重分析结论。当前仓库未接入 analysis 持久化表，返回占位结果")
    public Map<String, Object> saveWeightAnalysis(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "分析内容") String content,
            @ToolParam(description = "观察窗口说明") String observationWindow) {
        return Map.of(
                "saved", false,
                "reason", "当前仓库尚未配置 weight analysis 持久化表",
                "userId", userId,
                "content", content,
                "observationWindow", observationWindow
        );
    }
}
