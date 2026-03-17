package com.xhn.health.psychology.chatmemories.model;

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
 * 聊天记录表（health_psy_chat_memories）基础实体
 */
@Data
@TableName(value = "health_psy_chat_memories", autoResultMap = true)
@Schema(description = "聊天记录表")
public class BaseHealthPsyChatMemories {

    @Schema(description = "聊天记录独立主键，自增 BIGINT")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联的用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "角色（user/assistant/system）")
    @TableField("role")
    private String role;

    @Schema(description = "聊天内容")
    @TableField("content")
    private String content;

    @Schema(description = "情绪标签")
    @TableField("emotion_tags")
    private String emotionTags;

    @Schema(description = "内容向量（vector 文本，1536 维）")
    @TableField(value = "content_vector", typeHandler = VectorTypeHandler.class)
    private List<Double> contentVector;

    @Schema(description = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
