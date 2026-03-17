package com.xhn.health.mcp;

import com.xhn.health.activities.service.HealthyActivitiesService;
import com.xhn.health.dailysummaries.service.HealthyDailySummariesService;
import com.xhn.health.dietlogs.service.HealthyDietLogsService;
import com.xhn.health.goals.service.HealthyGoalsService;
import com.xhn.health.userprofiles.service.HealthyUserProfilesService;
import com.xhn.health.weightlogs.service.HealthyWeightLogsService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class HealthManagerMcpService {

    private final HealthyUserProfilesService userProfilesService;
    private final HealthyGoalsService goalsService;
    private final HealthyWeightLogsService weightLogsService;
    private final HealthyDietLogsService dietLogsService;
    private final HealthyActivitiesService activitiesService;
    private final HealthyDailySummariesService dailySummariesService;
    private final HealthMcpSupport support;

    public HealthManagerMcpService(
            HealthyUserProfilesService userProfilesService,
            HealthyGoalsService goalsService,
            HealthyWeightLogsService weightLogsService,
            HealthyDietLogsService dietLogsService,
            HealthyActivitiesService activitiesService,
            HealthyDailySummariesService dailySummariesService,
            HealthMcpSupport support) {
        this.userProfilesService = userProfilesService;
        this.goalsService = goalsService;
        this.weightLogsService = weightLogsService;
        this.dietLogsService = dietLogsService;
        this.activitiesService = activitiesService;
        this.dailySummariesService = dailySummariesService;
        this.support = support;
    }

    @Tool(name = "get_health_snapshot", description = "返回用户基础档案、目标以及近 N 天体重、饮食、活动摘要")
    public Map<String, Object> getHealthSnapshot(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "最近天数") Integer days) {
        int windowDays = days == null ? 7 : days;
        LocalDate[] range = support.dateRange(windowDays);

        BigDecimal dietCalories = dietLogsService.getDietLogsByUserId(userId).stream()
                .filter(log -> !log.getMealTime().toLocalDate().isBefore(range[0]) && !log.getMealTime().toLocalDate().isAfter(range[1]))
                .map(log -> support.safe(log.getTotalCalories()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal activityCalories = activitiesService.getActivitiesByUserId(userId).stream()
                .filter(log -> !log.getStartTime().toLocalDate().isBefore(range[0]) && !log.getStartTime().toLocalDate().isAfter(range[1]))
                .map(log -> support.safe(log.getCaloriesBurned()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalSteps = dailySummariesService.getSummariesByUserIdAndDateRange(userId, range[0], range[1]).stream()
                .mapToInt(log -> log.getTotalSteps() == null ? 0 : log.getTotalSteps())
                .sum();

        return Map.of(
                "profile", support.toMap(userProfilesService.getUserProfileByUserId(userId)),
                "goals", support.toList(goalsService.getGoalsByUserId(userId)),
                "window", Map.of("startDate", range[0], "endDate", range[1], "days", windowDays),
                "weightTrend", getIntegratedHealthInsights(userId, windowDays).get("weightTrend"),
                "dietCalories", support.round(dietCalories),
                "activityCalories", support.round(activityCalories),
                "totalSteps", totalSteps
        );
    }

    @Tool(name = "get_user_health_profile", description = "读取基础健康档案")
    public Map<String, Object> getUserHealthProfile(@ToolParam(description = "用户ID") Long userId) {
        return support.toMap(userProfilesService.getUserProfileByUserId(userId));
    }

    @Tool(name = "get_user_goals", description = "读取健康目标")
    public List<Map<String, Object>> getUserGoals(@ToolParam(description = "用户ID") Long userId) {
        return support.toList(goalsService.getGoalsByUserId(userId));
    }

    @Tool(name = "get_integrated_health_insights", description = "聚合体重、饮食和活动，输出统一分析上下文")
    public Map<String, Object> getIntegratedHealthInsights(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "最近天数") Integer days) {
        int windowDays = days == null ? 7 : days;
        LocalDate[] range = support.dateRange(windowDays);
        var weightLogs = weightLogsService.getWeightLogsByUserIdAndDateRange(userId, range[0], range[1]);

        Map<String, Object> weightTrend = Map.of(
                "count", weightLogs.size(),
                "latestWeightKg", weightLogs.isEmpty() ? null : support.round(weightLogs.get(weightLogs.size() - 1).getWeightKg())
        );

        Map<String, Object> insights = new LinkedHashMap<>();
        insights.put("profile", support.toMap(userProfilesService.getUserProfileByUserId(userId)));
        insights.put("goals", support.toList(goalsService.getGoalsByUserId(userId)));
        insights.put("weightTrend", weightTrend);
        insights.put("notes", List.of(
                "当前仓库只接入了体重、饮食、活动三类基础表",
                "情绪/心理上下文尚未接入，因此总控分析暂不包含心理维度"
        ));
        return insights;
    }

    @Tool(name = "save_health_advice", description = "保存总控建议。当前仓库未接入 advice 持久化表，返回占位结果")
    public Map<String, Object> saveHealthAdvice(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "建议类型") String adviceType,
            @ToolParam(description = "建议内容") String content,
            @ToolParam(description = "有效期截止日期") String validUntil) {
        return Map.of(
                "saved", false,
                "reason", "当前仓库尚未配置 health advice 持久化表",
                "userId", userId,
                "adviceType", adviceType,
                "content", content,
                "validUntil", validUntil
        );
    }

    @Tool(name = "save_followup_plan", description = "保存后续跟踪计划。当前仓库未接入 plan 持久化表，返回占位结果")
    public Map<String, Object> saveFollowupPlan(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "计划JSON") String planJson) {
        return Map.of(
                "saved", false,
                "reason", "当前仓库尚未配置 followup plan 持久化表",
                "userId", userId,
                "planJson", planJson
        );
    }
}
