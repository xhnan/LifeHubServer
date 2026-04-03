package com.xhn.health.agent.followupplans.model;

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
@TableName(value = "health_agent_followup_plans", autoResultMap = true)
@Schema(description = "Health agent followup plans")
public class BaseHealthAgentFollowupPlans {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("plan_type")
    private String planType;

    @TableField("title")
    private String title;

    @TableField("plan_json")
    private String planJson;

    @TableField("goal_summary")
    private String goalSummary;

    @TableField("related_advice_id")
    private Long relatedAdviceId;

    @TableField("status")
    private String status;

    @TableField("start_date")
    private LocalDate startDate;

    @TableField("end_date")
    private LocalDate endDate;

    @TableField(value = "followup_vector", typeHandler = VectorTypeHandler.class)
    private List<Double> followupVector;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
