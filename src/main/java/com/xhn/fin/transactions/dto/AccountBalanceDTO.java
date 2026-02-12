package com.xhn.fin.transactions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 资产/负债科目余额明细 DTO
 *
 * @author xhn
 * @date 2026-02-12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "科目余额明细")
public class AccountBalanceDTO {

    @Schema(description = "科目类型：ASSET/LIABILITY")
    private String accountType;

    @Schema(description = "合计余额")
    private BigDecimal total;

    @Schema(description = "各科目余额列表")
    private List<AccountItem> accounts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "科目余额项")
    public static class AccountItem {

        @Schema(description = "科目ID")
        private Long accountId;

        @Schema(description = "科目名称")
        private String accountName;

        @Schema(description = "科目图标")
        private String accountIcon;

        @Schema(description = "当前余额")
        private BigDecimal balance;
    }
}
