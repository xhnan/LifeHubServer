package com.xhn.health.goals.model;

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
 * 用户健康目标与里程碑表（healthy_goals）基础实体。
 */
@Data
@TableName(value = "healthy_goals", autoResultMap = true)
@Schema(description = "用户健康目标与里程碑表")
public class BaseHealthyGoals {

    @Schema(description = "目标记录独立主键，自增 BIGINT")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联的用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "目标类型（如 weight_loss、step_goal、muscle_gain）")
    @TableField("goal_type")
    private String goalType;

    @Schema(description = "目标数值")
    @TableField("target_value")
    private BigDecimal targetValue;

    @Schema(description = "期望达成日期")
    @TableField("deadline")
    private LocalDate deadline;

    @Schema(description = "状态（active、achieved、abandoned）")
    @TableField("status")
    private String status;

    @Schema(description = "目标语义向量（vector 文本，1024 维）")
    @TableField(value = "goal_embedding", typeHandler = VectorTypeHandler.class)
    private List<Double> goalEmbedding;

    @Schema(description = "数据写入时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
