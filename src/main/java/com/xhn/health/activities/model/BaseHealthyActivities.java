package com.xhn.health.activities.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xhn.base.mybatis.handler.VectorTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 单次主动运动与锻炼记录明细表（healthy_activities）基础实体。
 */
@Data
@TableName(value = "healthy_activities", autoResultMap = true)
@Schema(description = "单次主动运动与锻炼记录明细表")
public class BaseHealthyActivities {

    @Schema(description = "运动记录独立主键，自增 BIGINT")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联的用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "运动类型（如 running、swimming、weightlifting）")
    @TableField("activity_type")
    private String activityType;

    @Schema(description = "运动开始时间")
    @TableField("start_time")
    private LocalDateTime startTime;

    @Schema(description = "运动时长（分钟）")
    @TableField("duration_minutes")
    private Integer durationMinutes;

    @Schema(description = "消耗卡路里评估（千卡）")
    @TableField("calories_burned")
    private BigDecimal caloriesBurned;

    @Schema(description = "运动详情文本描述")
    @TableField("description")
    private String description;

    @Schema(description = "运动详情向量（vector 文本，512 维）")
    @TableField(value = "activity_embedding", typeHandler = VectorTypeHandler.class)
    private List<Double> activityEmbedding;

    @Schema(description = "数据写入时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
