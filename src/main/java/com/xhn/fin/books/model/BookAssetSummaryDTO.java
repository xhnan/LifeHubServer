package com.xhn.fin.books.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 账本资产概览 DTO
 *
 * @author xhn
 * @date 2026-02-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "账本资产概览")
public class BookAssetSummaryDTO {

    @Schema(description = "总资产")
    private BigDecimal totalAssets;

    @Schema(description = "总负债")
    private BigDecimal totalLiabilities;

    @Schema(description = "净资产（总资产 - 总负债）")
    private BigDecimal netAssets;
}
