package com.xhn.health.mcp;

import com.xhn.health.psychology.assessments.service.HealthPsyAssessmentsService;
import com.xhn.health.psychology.chatmemories.service.HealthPsyChatMemoriesService;
import com.xhn.health.psychology.dailymoods.model.HealthPsyDailyMoods;
import com.xhn.health.psychology.dailymoods.service.HealthPsyDailyMoodsService;
import com.xhn.health.psychology.knowledgebase.service.HealthPsyKnowledgeBaseService;
import com.xhn.health.psychology.profiles.service.HealthPsyProfilesService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class MentalSupportMcpService {

    private final HealthPsyProfilesService profilesService;
    private final HealthPsyDailyMoodsService dailyMoodsService;
    private final HealthPsyAssessmentsService assessmentsService;
    private final HealthPsyChatMemoriesService chatMemoriesService;
    private final HealthPsyKnowledgeBaseService knowledgeBaseService;
    private final HealthMcpSupport support;

    public MentalSupportMcpService(
            HealthPsyProfilesService profilesService,
            HealthPsyDailyMoodsService dailyMoodsService,
            HealthPsyAssessmentsService assessmentsService,
            HealthPsyChatMemoriesService chatMemoriesService,
            HealthPsyKnowledgeBaseService knowledgeBaseService,
            HealthMcpSupport support) {
        this.profilesService = profilesService;
        this.dailyMoodsService = dailyMoodsService;
        this.assessmentsService = assessmentsService;
        this.chatMemoriesService = chatMemoriesService;
        this.knowledgeBaseService = knowledgeBaseService;
        this.support = support;
    }

    @Tool(name = "get_user_psy_profile", description = "读取心理档案")
    public Map<String, Object> getUserPsyProfile(@ToolParam(description = "用户ID") Long userId) {
        return support.toMap(profilesService.getProfileByUserId(userId));
    }

    @Tool(name = "get_daily_moods", description = "读取每日情绪记录")
    public List<Map<String, Object>> getDailyMoods(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "开始日期 YYYY-MM-DD") String startDate,
            @ToolParam(description = "结束日期 YYYY-MM-DD") String endDate) {
        return support.toList(dailyMoodsService.getMoodsByUserIdAndDateRange(
                userId, support.parseDate(startDate), support.parseDate(endDate)));
    }

    @Tool(name = "get_mood_summary", description = "分析情绪均值、波动和主要情绪")
    public Map<String, Object> getMoodSummary(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "最近天数") Integer days) {
        var range = support.dateRange(days == null ? 7 : days);
        List<HealthPsyDailyMoods> moods = dailyMoodsService.getMoodsByUserIdAndDateRange(userId, range[0], range[1]);
        if (moods.isEmpty()) {
            return Map.of("count", 0);
        }
        double avg = moods.stream().mapToInt(item -> item.getMoodScore() == null ? 0 : item.getMoodScore()).average().orElse(0);
        int min = moods.stream().mapToInt(item -> item.getMoodScore() == null ? 0 : item.getMoodScore()).min().orElse(0);
        int max = moods.stream().mapToInt(item -> item.getMoodScore() == null ? 0 : item.getMoodScore()).max().orElse(0);
        String primary = moods.stream()
                .filter(item -> item.getPrimaryEmotion() != null)
                .collect(java.util.stream.Collectors.groupingBy(HealthPsyDailyMoods::getPrimaryEmotion, java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        return Map.of(
                "count", moods.size(),
                "averageMoodScore", avg,
                "minMoodScore", min,
                "maxMoodScore", max,
                "primaryEmotion", primary
        );
    }

    @Tool(name = "get_psy_assessments", description = "读取历史量表结果")
    public List<Map<String, Object>> getPsyAssessments(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "返回条数") Integer limit) {
        int size = limit == null ? 10 : limit;
        return support.toList(assessmentsService.getAssessmentsByUserId(userId).stream().limit(size).toList());
    }

    @Tool(name = "get_recent_chat_memories", description = "读取近期心理对话记忆")
    public List<Map<String, Object>> getRecentChatMemories(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "返回条数") Integer limit) {
        return support.toList(chatMemoriesService.getRecentChatMemoriesByUserId(userId, limit == null ? 10 : limit));
    }

    @Tool(name = "search_psy_knowledge", description = "从心理知识库检索支持内容")
    public List<Map<String, Object>> searchPsyKnowledge(
            @ToolParam(description = "查询词") String query,
            @ToolParam(description = "返回条数") Integer topK) {
        int size = topK == null ? 5 : topK;
        return support.toList(knowledgeBaseService.searchKnowledgeByTitle(query).stream()
                .sorted(Comparator.comparing(item -> item.getTitle() == null ? "" : item.getTitle()))
                .limit(size)
                .toList());
    }

    @Tool(name = "save_chat_memory", description = "保存对话记忆")
    public Map<String, Object> saveChatMemory(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "角色") String role,
            @ToolParam(description = "内容") String content,
            @ToolParam(description = "情绪标签") String emotionTags) {
        var entity = new com.xhn.health.psychology.chatmemories.model.HealthPsyChatMemories();
        entity.setUserId(userId);
        entity.setRole(role);
        entity.setContent(content);
        entity.setEmotionTags(emotionTags);
        boolean saved = chatMemoriesService.save(entity);
        return Map.of("saved", saved, "id", entity.getId(), "userId", userId);
    }

    @Tool(name = "save_assessment_result", description = "保存量表结果")
    public Map<String, Object> saveAssessmentResult(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "量表名") String scaleName,
            @ToolParam(description = "总分") Integer totalScore,
            @ToolParam(description = "严重等级") String severityLevel,
            @ToolParam(description = "结果分析") String resultAnalysis) {
        var entity = new com.xhn.health.psychology.assessments.model.HealthPsyAssessments();
        entity.setUserId(userId);
        entity.setScaleName(scaleName);
        entity.setTotalScore(totalScore);
        entity.setSeverityLevel(severityLevel);
        entity.setResultAnalysis(resultAnalysis);
        boolean saved = assessmentsService.save(entity);
        return Map.of("saved", saved, "id", entity.getId(), "userId", userId);
    }

    @Tool(name = "save_mood_support_advice", description = "保存心理支持建议。当前仓库无专门 advice 表，先作为聊天记忆落库")
    public Map<String, Object> saveMoodSupportAdvice(
            @ToolParam(description = "用户ID") Long userId,
            @ToolParam(description = "建议内容") String content,
            @ToolParam(description = "支持类型") String supportType) {
        return saveChatMemory(userId, "assistant", "[supportType=" + supportType + "] " + content, supportType);
    }
}
