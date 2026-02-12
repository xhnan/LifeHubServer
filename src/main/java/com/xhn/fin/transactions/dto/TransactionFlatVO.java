package com.xhn.fin.transactions.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Mapper 查询的扁平结果，用于组装流水账视图
 *
 * @author xhn
 * @date 2026-02-12
 */
@Data
public class TransactionFlatVO {

    private Long transId;
    private LocalDateTime transDate;
    private String description;
    private BigDecimal amount;

    // 分录信息
    private Long entryId;
    private Long accountId;
    private String accountName;
    private String accountIcon;
    private String accountType;
    private String direction;
    private BigDecimal entryAmount;
    private String memo;

    // 标签信息
    private Long tagId;
    private String tagName;
    private String tagColor;
    private String tagIcon;
}
