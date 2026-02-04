package com.xhn.fin.accounts.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务科目表实体基类
 * 对应数据库表: fin_accounts
 * @author xhn
 * @date 2026-02-04
 */
@Data
@TableName("fin_accounts")
@Schema(description = "财务科目表")
public class BaseFinAccounts {

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "账户名称 (如：招商银行、午餐)")
    @TableField("name")
    private String name;

    @Schema(description = "父级ID (实现树形结构，根节点为NULL)")
    @TableField("parent_id")
    private Long parentId;

    @Schema(description = "账户类型 (枚举值: ASSET, LIABILITY, EQUITY, INCOME, EXPENSE)")
    @TableField("account_type")
    private String accountType;

    @Schema(description = "币种代码 (如: CNY, USD)")
    @TableField("currency_code")
    private String currencyCode;

    @Schema(description = "期初余额 (用于系统初始化时的存量)")
    @TableField("initial_balance")
    private BigDecimal initialBalance;

    @Schema(description = "是否归档 (0:启用, 1:归档/逻辑删除)")
    @TableLogic
    @TableField("is_archived")
    private Integer isArchived;

    @Schema(description = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @Schema(description = "科目说明/备注：用于解释该科目的核算范围")
    @TableField("description")
    private String description;

    @Schema(description = "是否叶子节点：只有true的科目允许被录入凭证")
    @TableField("is_leaf")
    private Boolean isLeaf;

    @Schema(description = "业务编码")
    @TableField("code")
    private String code;
}