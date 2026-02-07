package com.xhn.fin.budgets.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预算表实体基类
 * 表名: fin_budgets
 * @author xhn
 * @date 2026-02-07
 */
@Data
@TableName("fin_budgets")
@Schema(description = "预算表")
public class BaseFinBudgets {

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联的账户科目ID")
    @TableField("account_id")
    private Long accountId;

    @Schema(description = "预算限额")
    private BigDecimal amount;

    @Schema(description = "预算周期类型 (MONTHLY, YEARLY)")
    @TableField("period_type")
    private String periodType;

    @Schema(description = "预算所属日期 (如 2026-02-01 代表2月)")
    @TableField("period_date")
    private LocalDate periodDate;

    @Schema(description = "关联用户ID (支持多用户独立预算)")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}