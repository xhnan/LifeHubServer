package com.xhn.fin.prices.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 金融价格数据表实体基类
 * 表名: fin_prices
 * @author xhn
 * @date 2026-02-04
 */
@Data
@TableName("fin_prices")
@Schema(description = "金融价格数据表")
public class BaseFinPrices {

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "标的物代码 (如 00700.HK)")
    @TableField("commodity_code")
    private String commodityCode;

    @Schema(description = "价格对应的时间点")
    @TableField("price_date")
    private LocalDateTime priceDate;

    @Schema(description = "收盘价/汇率")
    @TableField("close_price")
    private BigDecimal closePrice;

    @Schema(description = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}