package com.xhn.health.agent.checkins.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xhn.base.mybatis.handler.VectorTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "health_agent_checkins", autoResultMap = true)
@Schema(description = "Health agent checkins")
public class BaseHealthAgentCheckins {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("advice_record_id")
    private Long adviceRecordId;

    @TableField("followup_plan_id")
    private Long followupPlanId;

    @TableField("checkin_date")
    private LocalDate checkinDate;

    @TableField("completion_status")
    private String completionStatus;

    @TableField("adherence_score")
    private Integer adherenceScore;

    @TableField("effect_score")
    private Integer effectScore;

    @TableField("user_feedback")
    private String userFeedback;

    @TableField("blocker_reason")
    private String blockerReason;

    @TableField(value = "checkin_vector", typeHandler = VectorTypeHandler.class)
    private List<Double> checkinVector;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
