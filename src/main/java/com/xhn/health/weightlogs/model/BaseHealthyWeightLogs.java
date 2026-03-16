package com.xhn.health.weightlogs.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户体重与体脂记录明细表（healthy_weight_logs）基础实体。
 */
@Data
@TableName("healthy_weight_logs")
@Schema(description = "用户体重与体脂记录明细表")
public class BaseHealthyWeightLogs {

    @Schema(description = "体重记录独立主键，自增 BIGINT")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联的用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "记录日期")
    @TableField("record_date")
    private LocalDate recordDate;

    @Schema(description = "体重（千克）")
    @TableField("weight_kg")
    private BigDecimal weightKg;

    @Schema(description = "体脂率（百分比）")
    @TableField("body_fat_percentage")
    private BigDecimal bodyFatPercentage;

    @Schema(description = "身体质量指数（BMI）")
    @TableField("bmi")
    private BigDecimal bmi;

    @Schema(description = "数据写入时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
