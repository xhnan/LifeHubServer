package com.xhn.health.mcp;

import com.xhn.health.activities.model.HealthyActivities;
import com.xhn.health.activities.service.HealthyActivitiesService;
import com.xhn.health.dailysummaries.service.HealthyDailySummariesService;
import com.xhn.health.dietlogs.service.HealthyDietLogsService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ActivityMcpService {

    private final HealthyActivitiesService activitiesService;
    private final HealthyDailySummariesService dailySummariesService;
    private final HealthyDietLogsService dietLogsService;
    private final HealthMcpSupport support;

    public ActivityMcpService(
            HealthyActivitiesService activitiesService,
            HealthyDailySummariesService dailySummariesService,
            HealthyDietLogsService dietLogsService,
            HealthMcpSupport support) {
        this.activitiesService = activitiesService;
        this.dailySummariesService = dailySummariesService;
        this.dietLogsService = dietLogsService;
        this.support = support;
    }

    @Tool(name = "get_activity_logs", description = "读取运动活动明细")
    public List<Map<String, Object>> getActivityLogs(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "开始日期 YYYY-MM-DD") String startDate,
            @ToolParam(description = "结束日期 YYYY-MM-DD") String endDate) {
        LocalDate start = support.parseDate(startDate);
        LocalDate end = support.parseDate(endDate);
        List<HealthyActivities> logs = activitiesService.getActivitiesByUserId(userId).stream()
                .filter(log -> !log.getStartTime().toLocalDate().isBefore(start) && !log.getStartTime().toLocalDate().isAfter(end))
                .toList();
        return support.toList(logs);
    }

    @Tool(name = "get_daily_summaries", description = "读取每日步数、活动分钟数、卡路里摘要")
    public List<Map<String, Object>> getDailySummaries(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "开始日期 YYYY-MM-DD") String startDate,
            @ToolParam(description = "结束日期 YYYY-MM-DD") String endDate) {
        return support.toList(dailySummariesService.getSummariesByUserIdAndDateRange(
                userId, support.parseDate(startDate), support.parseDate(endDate)));
    }

    @Tool(name = "get_activity_summary", description = "汇总近 N 天活动量")
    public Map<String, Object> getActivitySummary(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "最近天数") Integer days) {
        LocalDate[] range = support.dateRange(days == null ? 7 : days);
        List<HealthyActivities> activities = activitiesService.getActivitiesByUserId(userId).stream()
                .filter(log -> !log.getStartTime().toLocalDate().isBefore(range[0]) && !log.getStartTime().toLocalDate().isAfter(range[1]))
                .toList();

        int totalDuration = activities.stream().mapToInt(item -> item.getDurationMinutes() == null ? 0 : item.getDurationMinutes()).sum();
        BigDecimal burned = activities.stream().map(item -> support.safe(item.getCaloriesBurned())).reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalSteps = dailySummariesService.getSummariesByUserIdAndDateRange(userId, range[0], range[1]).stream()
                .mapToInt(item -> item.getTotalSteps() == null ? 0 : item.getTotalSteps())
                .sum();

        return Map.of(
                "activityCount", activities.size(),
                "totalDurationMinutes", totalDuration,
                "activityCaloriesBurned", support.round(burned),
                "totalSteps", totalSteps
        );
    }

    @Tool(name = "get_activity_pattern_analysis", description = "分析久坐、活动不足和活动集中时段")
    public Map<String, Object> getActivityPatternAnalysis(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "最近天数") Integer days) {
        LocalDate[] range = support.dateRange(days == null ? 7 : days);
        List<HealthyActivities> activities = activitiesService.getActivitiesByUserId(userId).stream()
                .filter(log -> !log.getStartTime().toLocalDate().isBefore(range[0]) && !log.getStartTime().toLocalDate().isAfter(range[1]))
                .toList();

        Map<String, Long> typeDistribution = activities.stream()
                .collect(Collectors.groupingBy(HealthyActivities::getActivityType, LinkedHashMap::new, Collectors.counting()));

        long morning = activities.stream().filter(a -> a.getStartTime().getHour() < 12).count();
        long evening = activities.stream().filter(a -> a.getStartTime().getHour() >= 18).count();
        return Map.of(
                "typeDistribution", typeDistribution,
                "morningActivities", morning,
                "eveningActivities", evening,
                "observations", List.of(
                        activities.isEmpty() ? "近阶段没有活动记录" : "存在主动运动记录",
                        evening > morning ? "活动更集中在傍晚/晚间" : "活动时段较均衡或偏上午"
                )
        );
    }

    @Tool(name = "get_energy_balance_summary", description = "结合活动消耗和饮食摄入给出能量平衡分析")
    public Map<String, Object> getEnergyBalanceSummary(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "最近天数") Integer days) {
        LocalDate[] range = support.dateRange(days == null ? 7 : days);
        BigDecimal intake = dietLogsService.getDietLogsByUserId(userId).stream()
                .filter(log -> !log.getMealTime().toLocalDate().isBefore(range[0]) && !log.getMealTime().toLocalDate().isAfter(range[1]))
                .map(log -> support.safe(log.getTotalCalories()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal active = dailySummariesService.getSummariesByUserIdAndDateRange(userId, range[0], range[1]).stream()
                .map(log -> support.safe(log.getActiveCaloriesKcal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of(
                "intakeCalories", support.round(intake),
                "activeCalories", support.round(active),
                "estimatedBalance", support.round(intake.subtract(active))
        );
    }

    @Tool(name = "save_activity_advice", description = "保存活动建议。当前仓库未接入 advice 持久化表，返回占位结果")
    public Map<String, Object> saveActivityAdvice(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "建议内容") String content,
            @ToolParam(description = "有效期截止日期") String validUntil) {
        return Map.of(
                "saved", false,
                "reason", "当前仓库尚未配置 activity advice 持久化表",
                "userId", userId,
                "content", content,
                "validUntil", validUntil
        );
    }
}
