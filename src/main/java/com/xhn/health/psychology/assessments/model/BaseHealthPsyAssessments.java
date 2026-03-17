package com.xhn.health.psychology.assessments.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 心理评估表（health_psy_assessments）基础实体
 */
@Data
@TableName("health_psy_assessments")
@Schema(description = "心理评估表")
public class BaseHealthPsyAssessments {

    @Schema(description = "评估记录独立主键，自增 BIGINT")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联的用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "评估量表名称")
    @TableField("scale_name")
    private String scaleName;

    @Schema(description = "总分")
    @TableField("total_score")
    private Integer totalScore;

    @Schema(description = "严重程度")
    @TableField("severity_level")
    private String severityLevel;

    @Schema(description = "结果分析")
    @TableField("result_analysis")
    private String resultAnalysis;

    @Schema(description = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
