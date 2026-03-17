package com.xhn.health.mcp;

import com.xhn.health.dietlogs.service.HealthyDietLogsService;
import com.xhn.health.psychology.assessments.service.HealthPsyAssessmentsService;
import com.xhn.health.psychology.dailymoods.service.HealthPsyDailyMoodsService;
import com.xhn.health.risk.flags.model.HealthRiskFlags;
import com.xhn.health.risk.flags.service.HealthRiskFlagsService;
import com.xhn.health.weightlogs.service.HealthyWeightLogsService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RiskGuardMcpService {

    private final HealthyWeightLogsService weightLogsService;
    private final HealthyDietLogsService dietLogsService;
    private final HealthPsyDailyMoodsService dailyMoodsService;
    private final HealthPsyAssessmentsService assessmentsService;
    private final HealthRiskFlagsService riskFlagsService;
    private final HealthMcpSupport support;

    public RiskGuardMcpService(
            HealthyWeightLogsService weightLogsService,
            HealthyDietLogsService dietLogsService,
            HealthPsyDailyMoodsService dailyMoodsService,
            HealthPsyAssessmentsService assessmentsService,
            HealthRiskFlagsService riskFlagsService,
            HealthMcpSupport support) {
        this.weightLogsService = weightLogsService;
        this.dietLogsService = dietLogsService;
        this.dailyMoodsService = dailyMoodsService;
        this.assessmentsService = assessmentsService;
        this.riskFlagsService = riskFlagsService;
        this.support = support;
    }

    @Tool(name = "get_user_risk_context", description = "读取健康、体重、情绪和量表的风险上下文")
    public Map<String, Object> getUserRiskContext(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "最近天数") Integer days) {
        int windowDays = days == null ? 14 : days;
        var range = support.dateRange(windowDays);
        return Map.of(
                "weightLogs", support.toList(weightLogsService.getWeightLogsByUserIdAndDateRange(userId, range[0], range[1])),
                "dietLogs", support.toList(dietLogsService.getDietLogsByUserId(userId).stream()
                        .filter(log -> !log.getMealTime().toLocalDate().isBefore(range[0]) && !log.getMealTime().toLocalDate().isAfter(range[1]))
                        .toList()),
                "dailyMoods", support.toList(dailyMoodsService.getMoodsByUserIdAndDateRange(userId, range[0], range[1])),
                "assessments", support.toList(assessmentsService.getAssessmentsByUserId(userId).stream().limit(10).toList()),
                "riskHistory", support.toList(riskFlagsService.getRiskFlagsByUserId(userId).stream().limit(10).toList())
        );
    }

    @Tool(name = "detect_health_risks", description = "识别异常减重、极端节食等健康风险")
    public Map<String, Object> detectHealthRisks(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "最近天数") Integer days) {
        int windowDays = days == null ? 14 : days;
        var range = support.dateRange(windowDays);
        var weightLogs = weightLogsService.getWeightLogsByUserIdAndDateRange(userId, range[0], range[1]);
        var dietLogs = dietLogsService.getDietLogsByUserId(userId).stream()
                .filter(log -> !log.getMealTime().toLocalDate().isBefore(range[0]) && !log.getMealTime().toLocalDate().isAfter(range[1]))
                .toList();

        List<String> findings = new ArrayList<>();
        if (weightLogs.size() >= 2) {
            BigDecimal delta = support.safe(weightLogs.get(weightLogs.size() - 1).getWeightKg())
                    .subtract(support.safe(weightLogs.get(0).getWeightKg()));
            if (delta.compareTo(BigDecimal.valueOf(-3)) <= 0) {
                findings.add("近阶段体重下降超过 3kg，建议检查是否存在异常减重");
            }
        }
        long lowIntakeDays = dietLogs.stream()
                .filter(log -> support.safe(log.getTotalCalories()).compareTo(BigDecimal.valueOf(800)) < 0)
                .count();
        if (lowIntakeDays > 0) {
            findings.add("存在低热量摄入记录，需留意极端节食风险");
        }
        return Map.of("riskType", "health", "findings", findings);
    }

    @Tool(name = "detect_psy_risks", description = "识别高风险心理信号")
    public Map<String, Object> detectPsyRisks(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "最近天数") Integer days) {
        int windowDays = days == null ? 14 : days;
        var range = support.dateRange(windowDays);
        var moods = dailyMoodsService.getMoodsByUserIdAndDateRange(userId, range[0], range[1]);
        var assessments = assessmentsService.getAssessmentsByUserId(userId);

        List<String> findings = new ArrayList<>();
        long lowMoodDays = moods.stream().filter(item -> item.getMoodScore() != null && item.getMoodScore() <= 3).count();
        if (lowMoodDays >= 3) {
            findings.add("连续低情绪记录较多，建议重点关注");
        }
        assessments.stream().findFirst().ifPresent(item -> {
            if ("high".equalsIgnoreCase(item.getSeverityLevel()) || "severe".equalsIgnoreCase(item.getSeverityLevel())) {
                findings.add("最近心理量表结果提示高风险等级");
            }
        });
        return Map.of("riskType", "psychology", "findings", findings);
    }

    @Tool(name = "save_risk_flag", description = "记录风险标记")
    public Map<String, Object> saveRiskFlag(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "风险类型") String riskType,
            @ToolParam(description = "风险等级") String level,
            @ToolParam(description = "原因") String reason) {
        HealthRiskFlags entity = new HealthRiskFlags();
        entity.setUserId(userId);
        entity.setRiskType(riskType);
        entity.setLevel(level);
        entity.setReason(reason);
        boolean saved = riskFlagsService.save(entity);
        return Map.of("saved", saved, "id", entity.getId(), "userId", userId);
    }

    @Tool(name = "get_risk_history", description = "读取历史风险事件")
    public List<Map<String, Object>> getRiskHistory(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "返回条数") Integer limit) {
        int size = limit == null ? 10 : limit;
        return support.toList(riskFlagsService.getRiskFlagsByUserId(userId).stream().limit(size).toList());
    }
}
