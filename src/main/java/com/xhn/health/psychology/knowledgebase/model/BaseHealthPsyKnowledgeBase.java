package com.xhn.health.psychology.knowledgebase.model;

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

/**
 * 心理知识库表（health_psy_knowledge_base）基础实体
 */
@Data
@TableName(value = "health_psy_knowledge_base", autoResultMap = true)
@Schema(description = "心理知识库表")
public class BaseHealthPsyKnowledgeBase {

    @Schema(description = "知识库记录独立主键，自增 BIGINT")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "知识标题")
    @TableField("title")
    private String title;

    @Schema(description = "知识分类")
    @TableField("category")
    private String category;

    @Schema(description = "知识内容")
    @TableField("content")
    private String content;

    @Schema(description = "内容向量（vector 文本，1536 维）")
    @TableField(value = "content_vector", typeHandler = VectorTypeHandler.class)
    private List<Double> contentVector;

    @Schema(description = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
