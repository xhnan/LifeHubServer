package com.xhn.fin.books.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 账本表实体基类
 * 表名: fin_books
 * @author xhn
 * @date 2026-02-07
 */
@Data
@TableName("fin_books")
@Schema(description = "账本表实体基类")
public class BaseFinBooks {

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "账本名称")
    @TableField("name")
    private String name;

    @Schema(description = "账本描述")
    @TableField("description")
    private String description;

    @Schema(description = "拥有者ID")
    @TableField("owner_id")
    private Long ownerId;

    @Schema(description = "默认币种 (CNY/SGD)")
    @TableField("default_currency")
    private String defaultCurrency;

    @Schema(description = "账本封面图片URL")
    @TableField("cover_url")
    private String coverUrl;

    @Schema(description = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}