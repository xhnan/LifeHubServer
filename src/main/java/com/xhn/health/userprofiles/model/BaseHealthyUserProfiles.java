package com.xhn.health.userprofiles.model;

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
 * 健康助手特化的用户画像扩展表（healthy_user_profiles）基础实体。
 */
@Data
@TableName(value = "healthy_user_profiles", autoResultMap = true)
@Schema(description = "健康助手特化的用户画像扩展表")
public class BaseHealthyUserProfiles {

    @Schema(description = "画像记录独立主键，自增 BIGINT")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联主用户表的唯一标识")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "出生日期，用于动态计算年龄和基础代谢")
    @TableField("birth_date")
    private LocalDate birthDate;

    @Schema(description = "性别生理特征，影响卡路里计算公式")
    @TableField("gender")
    private String gender;

    @Schema(description = "身高（厘米）")
    @TableField("height_cm")
    private BigDecimal heightCm;

    @Schema(description = "初始体重（千克）")
    @TableField("baseline_weight_kg")
    private BigDecimal baselineWeightKg;

    @Schema(description = "目标体重（千克）")
    @TableField("target_weight_kg")
    private BigDecimal targetWeightKg;

    @Schema(description = "健康特征与偏好的向量表示（vector 文本，1024 维）")
    @TableField(value = "health_profile_embedding", typeHandler = VectorTypeHandler.class)
    private List<Double> healthProfileEmbedding;

    @Schema(description = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
