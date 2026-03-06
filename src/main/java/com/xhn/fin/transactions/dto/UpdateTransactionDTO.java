package com.xhn.fin.transactions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 修改交易记录 DTO
 * 用于更新交易记录及其分录
 *
 * @author xhn
 * @date 2026-03-06
 */
@Data
@Schema(description = "修改交易记录请求")
public class UpdateTransactionDTO {

    @Schema(description = "交易记录ID（已废弃，请使用路径参数）", deprecated = true, example = "123")
    private Long transId;

    @Schema(description = "交易日期", example = "2026-03-06T12:00:00")
    private LocalDateTime transDate;

    @Schema(description = "交易描述", example = "周末超市采购")
    private String description;

    @Schema(description = "附件ID（关联文件对象）")
    private String attachmentId;

    @Schema(description = "分录列表（必填，更新时会删除旧行分录并创建新分录）", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<TransactionEntryDTO.EntryRequest> entries;

    @Schema(description = "标签ID列表", example = "[1, 2, 3]")
    private List<Long> tagIds;
}
