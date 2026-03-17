package com.xhn.health.psychology.dailymoods.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日心情记录表（health_psy_daily_moods）基础实体
 */
@Data
@TableName("health_psy_daily_moods")
@Schema(description = "每日心情记录表")
public class BaseHealthPsyDailyMoods {

    @Schema(description = "心情记录独立主键，自增 BIGINT")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联的用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "心情评分（1-10）")
    @TableField("mood_score")
    private Integer moodScore;

    @Schema(description = "主要情绪")
    @TableField("primary_emotion")
    private String primaryEmotion;

    @Schema(description = "日记文本")
    @TableField("journal_text")
    private String journalText;

    @Schema(description = "记录日期")
    @TableField("record_date")
    private LocalDate recordDate;

    @Schema(description = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
