package com.xhn.fin.entries.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 财务分录表 基础实体类
 * @author xhn
 * @date 2026-02-07
 */
@Data
@TableName("fin_entries")
@Schema(description = "财务分录表")
public class BaseFinEntries {

    @Schema(description = "主键ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "关联主交易表的ID")
    @TableField("trans_id")
    private Long transId;

    @Schema(description = "关联账户表的ID")
    @TableField("account_id")
    private Long accountId;

    @Schema(description = "资金流动方向：DEBIT(借)/CREDIT(贷)")
    @TableField("direction")
    private String direction;

    @Schema(description = "交易金额(绝对值，必须为正数)")
    @TableField("amount")
    private BigDecimal amount;

    @Schema(description = "分录级别的详细备注")
    @TableField("memo")
    private String memo;

    @Schema(description = "资产数量(如股票股数)")
    @TableField("quantity")
    private BigDecimal quantity;

    @Schema(description = "价格")
    @TableField("price")
    private BigDecimal price;

    @Schema(description = "商品代码")
    @TableField("commodity_code")
    private String commodityCode;

    @Schema(description = "用户id")
    @TableField("user_id")
    private Long userId;
}