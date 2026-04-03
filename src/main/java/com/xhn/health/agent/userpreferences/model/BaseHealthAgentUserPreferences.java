package com.xhn.health.agent.userpreferences.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xhn.base.mybatis.handler.VectorTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "health_agent_user_preferences", autoResultMap = true)
@Schema(description = "Health agent user preferences")
public class BaseHealthAgentUserPreferences {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("preferred_diet_style")
    private String preferredDietStyle;

    @TableField("disliked_foods")
    private String dislikedFoods;

    @TableField("preferred_exercise_types")
    private String preferredExerciseTypes;

    @TableField("preferred_support_style")
    private String preferredSupportStyle;

    @TableField("routine_pattern")
    private String routinePattern;

    @TableField("motivation_tags")
    private String motivationTags;

    @TableField("habit_profile")
    private String habitProfile;

    @TableField(value = "preference_vector", typeHandler = VectorTypeHandler.class)
    private List<Double> preferenceVector;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
