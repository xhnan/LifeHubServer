package com.xhn.fin.transactions.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 财务交易记录表实体基类
 * 表名: fin_transactions
 * @author xhn
 * @date 2026-02-04
 */
@Data
@TableName("fin_transactions")
@Schema(description = "财务交易记录表")
public class BaseFinTransactions {

    @Schema(description = "主键ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "交易发生的实际时间")
    @TableField("trans_date")
    private LocalDateTime transDate;

    @Schema(description = "交易描述")
    private String description;

    @Schema(description = "附件ID(关联MinIO文件对象)")
    @TableField("attachment_id")
    private String attachmentId;

    @Schema(description = "创建人ID(关联RBAC用户表)")
    @TableField("created_by")
    private Long createdBy;

    @Schema(description = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}