package com.xhn.health.dailysummaries.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xhn.base.mybatis.handler.VectorTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 每日整体活动与消耗汇总表（healthy_daily_summaries）基础实体。
 */
@Data
@TableName(value = "health_daily_summaries", autoResultMap = true)
@Schema(description = "每日整体活动与消耗汇总表")
public class BaseHealthyDailySummaries {

    @Schema(description = "汇总记录独立主键，自增 BIGINT")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联的用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "记录日期")
    @TableField("record_date")
    private LocalDate recordDate;

    @Schema(description = "当日累计总步数")
    @TableField("total_steps")
    private Integer totalSteps;

    @Schema(description = "活动消耗卡路里（千卡）")
    @TableField("active_calories_kcal")
    private BigDecimal activeCaloriesKcal;

    @Schema(description = "静息/基础代谢消耗卡路里（千卡）")
    @TableField("resting_calories_kcal")
    private BigDecimal restingCaloriesKcal;

    @Schema(description = "当日累计估算距离（米）")
    @TableField("total_distance_meters")
    private BigDecimal totalDistanceMeters;

    @Schema(description = "当日活跃/运动总时长（分钟）")
    @TableField("active_minutes")
    private Integer activeMinutes;

    @Schema(description = "当日活动自然语言总结向量（vector 文本，1024 维）")
    @TableField(value = "daily_context_embedding", typeHandler = VectorTypeHandler.class)
    private List<Double> dailyContextEmbedding;

    @Schema(description = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
