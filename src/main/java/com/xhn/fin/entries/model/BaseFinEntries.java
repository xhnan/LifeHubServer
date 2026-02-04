package com.xhn.fin.entries.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 分录明细表实体基类
 * 表名: fin_entries
 * @author xhn
 * @date 2026-02-04
 */
@Data
@TableName("fin_entries")
@Schema(description = "分录明细表")
public class BaseFinEntries {

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联交易主表ID")
    @TableField("trans_id")
    private Long transId;

    @Schema(description = "关联账户表ID")
    @TableField("account_id")
    private Long accountId;

    @Schema(description = "借贷方向 (1:借/Debit, -1:贷/Credit)")
    @TableField("direction")
    private Integer direction;

    @Schema(description = "发生金额 (本币金额，用于报表统计)")
    @TableField("amount")
    private BigDecimal amount;

    @Schema(description = "数量 (投资专用：如 100 股, 0.5 克黄金)")
    @TableField("quantity")
    private BigDecimal quantity;

    @Schema(description = "单价 (投资专用：记录交易时的成交单价)")
    @TableField("price")
    private BigDecimal price;

    @Schema(description = "标的物代码 (如 TENCENT, USD，为空则默认本币)")
    @TableField("commodity_code")
    private String commodityCode;
}