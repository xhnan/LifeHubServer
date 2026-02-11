package com.xhn.fin.transactions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 本月收支统计 DTO
 *
 * @author xhn
 * @date 2026-02-10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "本月收支统计")
public class MonthlyStatisticsDTO {

    @Schema(description = "本月收入总额")
    private BigDecimal totalIncome;

    @Schema(description = "本月支出总额")
    private BigDecimal totalExpense;

    @Schema(description = "本月结余（收入 - 支出）")
    private BigDecimal balance;
}
