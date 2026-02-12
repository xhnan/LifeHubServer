package com.xhn.fin.transactions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 分类排行 DTO
 *
 * @author xhn
 * @date 2026-02-12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分类排行统计")
public class CategoryRankDTO {

    @Schema(description = "统计类型：EXPENSE(支出)/INCOME(收入)")
    private String type;

    @Schema(description = "合计金额")
    private BigDecimal total;

    @Schema(description = "分类排行列表（按金额降序）")
    private List<CategoryItem> categories;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "分类项")
    public static class CategoryItem {

        @Schema(description = "科目ID")
        private Long accountId;

        @Schema(description = "科目名称")
        private String accountName;

        @Schema(description = "科目图标")
        private String accountIcon;

        @Schema(description = "金额")
        private BigDecimal amount;

        @Schema(description = "占比（百分比，如 35.5）")
        private BigDecimal percentage;
    }
}
