package com.xhn.sys.template.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 模板实体类
 * @author xhn
 * @date 2025-01-22
 */
@Data
@TableName("sys_template")
public class BaseSysTemplate {
    /**
     * 模板ID，主键，使用雪花ID
     */
    @TableId(value = "template_id", type = IdType.ASSIGN_ID)
    private Long templateId;

    /**
     * 模板名称
     */
    @TableField("template_name")
    private String templateName;

    /**
     * 模板类型
     */
    @TableField("template_type")
    private String templateType;

    /**
     * 模板内容，存储模板的具体文本
     */
    @TableField("template_content")
    private String templateContent;

    /**
     * 模板状态（例如：active, inactive, archived）
     */
    @TableField("status")
    private String status;

    /**
     * 模板创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 模板最后更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 软删除时间
     */
    @TableField("deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 软删除标志
     */
    @TableField("is_deleted")
    private Boolean isDeleted;

    /**
     * 创建人ID
     */
    @TableField("creator_id")
    private Long creatorId;

    /**
     * 更新人ID
     */
    @TableField("updater_id")
    private Long updaterId;

    /**
     * 模板描述
     */
    @TableField("description")
    private String description;
}