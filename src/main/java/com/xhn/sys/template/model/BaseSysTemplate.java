package com.xhn.sys.template.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 模板实体类
 *
 * @author 系统生成
 */
@Data
@TableName("sys_template")
public class BaseSysTemplate {

    /**
     * 模板ID，主键，使用雪花ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long templateId;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板类型
     */
    private String templateType;

    /**
     * 模板内容，存储模板的具体文本
     */
    private String templateContent;

    /**
     * 模板状态（例如：active, inactive, archived）
     */
    private String status;

    /**
     * 模板创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 模板最后更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 软删除时间
     */
    private LocalDateTime deletedAt;

    /**
     * 软删除标志
     */
    private Boolean isDeleted;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 更新人ID
     */
    private Long updaterId;

    /**
     * 模板描述
     */
    private String description;
}