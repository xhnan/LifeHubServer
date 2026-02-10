package com.xhn.fin.transactions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 交易创建响应 DTO
 *
 * @author xhn
 * @date 2026-02-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "交易创建响应")
public class TransactionEntryResponseDTO {

    @Schema(description = "交易ID")
    private Long transId;

    @Schema(description = "交易日期")
    private LocalDateTime transDate;

    @Schema(description = "交易描述")
    private String description;
}
