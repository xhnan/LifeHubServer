package com.xhn.fin.transactions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易明细 DTO — 流水账视图
 * 按日期分组，每笔交易展示收支方向、金额、科目等信息
 *
 * @author xhn
 * @date 2026-02-12
 */
@Data
@Schema(description = "交易明细（流水账视图）")
public class TransactionDetailDTO {

    @Schema(description = "按日期分组的交易列表")
    private List<DailyGroup> dailyGroups;

    @Schema(description = "总记录数")
    private long total;

    @Schema(description = "当前页码")
    private int pageNum;

    @Schema(description = "每页数量")
    private int pageSize;

    /**
     * 按日期分组
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "日分组")
    public static class DailyGroup {

        @Schema(description = "日期", example = "2026-02-12")
        private LocalDate date;

        @Schema(description = "当日收入合计")
        private BigDecimal dailyIncome;

        @Schema(description = "当日支出合计")
        private BigDecimal dailyExpense;

        @Schema(description = "当日交易列表")
        private List<TransactionItem> transactions;
    }

    /**
     * 单笔交易流水
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "单笔交易流水")
    public static class TransactionItem {

        @Schema(description = "交易ID")
        private Long transId;

        @Schema(description = "交易时间")
        private LocalDateTime transDate;

        @Schema(description = "交易类型：INCOME(收入)/EXPENSE(支出)/TRANSFER(转账)/OTHER(其他)")
        private String transType;

        @Schema(description = "显示金额（正数=收入，负数=支出）")
        private BigDecimal displayAmount;

        @Schema(description = "交易描述")
        private String description;

        @Schema(description = "主科目名称（如：午餐、工资）")
        private String categoryName;

        @Schema(description = "主科目图标")
        private String categoryIcon;

        @Schema(description = "对方科目名称（如：支付宝、招商银行）")
        private String targetAccountName;

        @Schema(description = "对方科目图标")
        private String targetAccountIcon;

        @Schema(description = "标签列表")
        private List<TagInfo> tags;
    }

    /**
     * 标签信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "标签信息")
    public static class TagInfo {

        @Schema(description = "标签ID")
        private Long tagId;

        @Schema(description = "标签名称")
        private String tagName;

        @Schema(description = "标签颜色")
        private String color;

        @Schema(description = "标签图标")
        private String icon;
    }
}
