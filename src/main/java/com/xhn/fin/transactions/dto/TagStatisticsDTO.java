package com.xhn.fin.transactions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 标签统计 DTO
 *
 * @author xhn
 * @date 2026-02-12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "标签统计")
public class TagStatisticsDTO {

    @Schema(description = "合计金额")
    private BigDecimal total;

    @Schema(description = "标签统计列表（按金额降序）")
    private List<TagItem> tags;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "标签统计项")
    public static class TagItem {

        @Schema(description = "标签ID")
        private Long tagId;

        @Schema(description = "标签名称")
        private String tagName;

        @Schema(description = "标签颜色")
        private String color;

        @Schema(description = "标签图标")
        private String icon;

        @Schema(description = "金额")
        private BigDecimal amount;

        @Schema(description = "交易笔数")
        private int count;

        @Schema(description = "占比（百分比）")
        private BigDecimal percentage;
    }
}
