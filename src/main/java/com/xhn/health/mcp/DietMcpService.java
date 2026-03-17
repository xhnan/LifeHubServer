package com.xhn.health.mcp;

import com.xhn.health.dietlogs.model.HealthyDietLogs;
import com.xhn.health.dietlogs.service.HealthyDietLogsService;
import com.xhn.health.goals.service.HealthyGoalsService;
import com.xhn.health.userprofiles.service.HealthyUserProfilesService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DietMcpService {

    private final HealthyDietLogsService dietLogsService;
    private final HealthyGoalsService goalsService;
    private final HealthyUserProfilesService userProfilesService;
    private final HealthMcpSupport support;

    public DietMcpService(
            HealthyDietLogsService dietLogsService,
            HealthyGoalsService goalsService,
            HealthyUserProfilesService userProfilesService,
            HealthMcpSupport support) {
        this.dietLogsService = dietLogsService;
        this.goalsService = goalsService;
        this.userProfilesService = userProfilesService;
        this.support = support;
    }

    @Tool(name = "get_diet_logs", description = "读取饮食明细")
    public List<Map<String, Object>> getDietLogs(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "开始日期 YYYY-MM-DD") String startDate,
            @ToolParam(description = "结束日期 YYYY-MM-DD") String endDate) {
        LocalDate start = support.parseDate(startDate);
        LocalDate end = support.parseDate(endDate);
        List<HealthyDietLogs> logs = dietLogsService.getDietLogsByUserId(userId).stream()
                .filter(log -> !log.getMealTime().toLocalDate().isBefore(start) && !log.getMealTime().toLocalDate().isAfter(end))
                .toList();
        return support.toList(logs);
    }

    @Tool(name = "get_diet_summary", description = "聚合热量、蛋白质、碳水、脂肪和餐次分布")
    public Map<String, Object> getDietSummary(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "最近天数") Integer days) {
        LocalDate[] range = support.dateRange(days == null ? 7 : days);
        List<HealthyDietLogs> logs = dietLogsService.getDietLogsByUserId(userId).stream()
                .filter(log -> !log.getMealTime().toLocalDate().isBefore(range[0]) && !log.getMealTime().toLocalDate().isAfter(range[1]))
                .toList();

        BigDecimal calories = BigDecimal.ZERO;
        BigDecimal protein = BigDecimal.ZERO;
        BigDecimal carbs = BigDecimal.ZERO;
        BigDecimal fat = BigDecimal.ZERO;
        for (HealthyDietLogs log : logs) {
            calories = calories.add(support.safe(log.getTotalCalories()));
            protein = protein.add(support.safe(log.getProteinG()));
            carbs = carbs.add(support.safe(log.getCarbsG()));
            fat = fat.add(support.safe(log.getFatG()));
        }

        Map<String, Long> mealDistribution = logs.stream()
                .collect(Collectors.groupingBy(HealthyDietLogs::getMealType, LinkedHashMap::new, Collectors.counting()));

        return Map.of(
                "days", days == null ? 7 : days,
                "logCount", logs.size(),
                "totalCalories", support.round(calories),
                "totalProteinG", support.round(protein),
                "totalCarbsG", support.round(carbs),
                "totalFatG", support.round(fat),
                "mealDistribution", mealDistribution
        );
    }

    @Tool(name = "get_meal_pattern_analysis", description = "分析早餐缺失、夜宵频繁、晚餐偏重等规律")
    public Map<String, Object> getMealPatternAnalysis(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "最近天数") Integer days) {
        LocalDate[] range = support.dateRange(days == null ? 7 : days);
        List<HealthyDietLogs> logs = dietLogsService.getDietLogsByUserId(userId).stream()
                .filter(log -> !log.getMealTime().toLocalDate().isBefore(range[0]) && !log.getMealTime().toLocalDate().isAfter(range[1]))
                .toList();

        long breakfast = logs.stream().filter(log -> "breakfast".equalsIgnoreCase(log.getMealType())).count();
        long dinner = logs.stream().filter(log -> "dinner".equalsIgnoreCase(log.getMealType())).count();
        long lateNight = logs.stream().filter(log -> log.getMealTime().getHour() >= 21).count();

        return Map.of(
                "breakfastCount", breakfast,
                "dinnerCount", dinner,
                "lateNightMeals", lateNight,
                "observations", List.of(
                        breakfast == 0 ? "近阶段未记录早餐" : "存在早餐记录",
                        lateNight > 0 ? "存在夜间进食" : "未发现明显夜宵模式",
                        dinner > breakfast ? "晚餐记录数高于早餐" : "三餐分布相对均衡"
                )
        );
    }

    @Tool(name = "get_diet_goal_alignment", description = "分析当前饮食是否符合减脂、维持或增肌目标")
    public Map<String, Object> getDietGoalAlignment(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "最近天数") Integer days) {
        return Map.of(
                "profile", support.toMap(userProfilesService.getUserProfileByUserId(userId)),
                "goals", support.toList(goalsService.getGoalsByUserId(userId)),
                "dietSummary", getDietSummary(userId, days),
                "notes", List.of("第一版仅提供基础汇总，尚未接入复杂目标规则引擎")
        );
    }

    @Tool(name = "save_diet_advice", description = "保存饮食建议。当前仓库未接入 advice 持久化表，返回占位结果")
    public Map<String, Object> saveDietAdvice(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "建议内容") String content,
            @ToolParam(description = "重点餐次") String mealFocus,
            @ToolParam(description = "有效期截止日期") String validUntil) {
        return Map.of(
                "saved", false,
                "reason", "当前仓库尚未配置 diet advice 持久化表",
                "userId", userId,
                "content", content,
                "mealFocus", mealFocus,
                "validUntil", validUntil
        );
    }
}
