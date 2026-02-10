package com.xhn.fin.tags.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 财务标签表实体基类
 * 
 * @author xhn
 * @date 2026-02-10
 */
@Data
@TableName("fin_tags")
@Schema(description = "财务标签表")
public class BaseFinTags {

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "标签名称 (如：出差、装修、宠物)")
    @TableField("tag_name")
    private String tagName;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @Schema(description = "标签颜色")
    private String color;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;
}