package com.xhn.fin.transactions.dto;

import com.xhn.fin.entries.model.FinEntries;
import com.xhn.fin.transactions.model.FinTransactions;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易及分录统一记账 DTO
 * 用于创建包含交易主表和分录表的完整记账记录
 *
 * @author xhn
 * @date 2026-02-10
 */
@Data
@Schema(description = "交易及分录统一记账请求")
public class TransactionEntryDTO {

    @Schema(description = "交易日期")
    private LocalDateTime transDate;

    @Schema(description = "交易描述", example = "周末超市采购")
    private String description;

    @Schema(description = "附件ID（关联文件对象）")
    private String attachmentId;

    @Schema(description = "所属账本ID")
    private Long bookId;

    @Schema(description = "分录列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<EntryRequest> entries;

    /**
     * 分录请求
     */
    @Data
    @Schema(description = "分录请求")
    public static class EntryRequest {

        @Schema(description = "科目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
        private Long accountId;

        @Schema(description = "借贷方向：DEBIT(借)/CREDIT(贷)", requiredMode = Schema.RequiredMode.REQUIRED, example = "DEBIT")
        private String direction;

        @Schema(description = "交易金额（绝对值，必须为正数）", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
        private String amount;

        @Schema(description = "分录备注")
        private String memo;

        @Schema(description = "资产数量（如股票股数）")
        private String quantity;

        @Schema(description = "价格")
        private String price;

        @Schema(description = "商品代码")
        private String commodityCode;
    }
}
