package com.xhn.health.psychology.profiles.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 心理档案表（health_psy_profiles）基础实体
 */
@Data
@TableName("health_psy_profiles")
@Schema(description = "心理档案表")
public class BaseHealthPsyProfiles {

    @Schema(description = "档案记录独立主键，自增 BIGINT")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联的用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "MBTI性格类型")
    @TableField("mbti_type")
    private String mbtiType;

    @Schema(description = "九型人格类型")
    @TableField("enneagram_type")
    private String enneagramType;

    @Schema(description = "基线压力水平（0-10）")
    @TableField("baseline_stress_level")
    private Integer baselineStressLevel;

    @Schema(description = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
