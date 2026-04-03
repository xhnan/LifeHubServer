package com.xhn.health.agent.advicerecords.model;

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
@TableName(value = "health_agent_advice_records", autoResultMap = true)
@Schema(description = "Health agent advice records")
public class BaseHealthAgentAdviceRecords {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("agent_type")
    private String agentType;

    @TableField("advice_type")
    private String adviceType;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("source_summary")
    private String sourceSummary;

    @TableField("source_snapshot")
    private String sourceSnapshot;

    @TableField("priority_level")
    private String priorityLevel;

    @TableField("status")
    private String status;

    @TableField("valid_until")
    private LocalDate validUntil;

    @TableField(value = "advice_vector", typeHandler = VectorTypeHandler.class)
    private List<Double> adviceVector;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
