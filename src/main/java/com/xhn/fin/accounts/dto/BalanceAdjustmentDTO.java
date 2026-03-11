package com.xhn.fin.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * 账户余额调整请求 DTO
 *
 * @author xhn
 * @date 2026-03-10
 */
@Data
@Schema(description = "账户余额调整请求")
public class BalanceAdjustmentDTO {

    @Schema(description = "账户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    @NotNull(message = "账户ID不能为空")
    private Long accountId;

    @Schema(description = "目标余额", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000.00")
    @NotNull(message = "目标余额不能为空")
    private BigDecimal targetBalance;

    @Schema(description = "交易描述", example = "余额调整")
    private String description;

    @Schema(description = "权益科目ID（余额调整科目，用于记录权益变动）", example = "200")
    private Long equityAccountId;
}
