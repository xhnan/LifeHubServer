package com.xhn.fin.transactions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 年度收支趋势 DTO
 *
 * @author xhn
 * @date 2026-02-12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "年度收支趋势")
public class YearlyTrendDTO {

    @Schema(description = "年份")
    private int year;

    @Schema(description = "月度数据列表（1-12月）")
    private List<MonthData> months;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "单月数据")
    public static class MonthData {

        @Schema(description = "月份 (1-12)")
        private int month;

        @Schema(description = "收入总额")
        private BigDecimal income;

        @Schema(description = "支出总额")
        private BigDecimal expense;

        @Schema(description = "结余")
        private BigDecimal balance;
    }
}
