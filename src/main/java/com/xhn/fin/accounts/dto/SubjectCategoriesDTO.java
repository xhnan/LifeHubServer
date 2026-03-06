package com.xhn.fin.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 科目分类数据传输对象
 * 用于返回按收入/支出分类的科目数据
 *
 * @author xhn
 * @date 2026-03-05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "科目分类响应")
public class SubjectCategoriesDTO {

    @Schema(description = "支出科目分类")
    private ExpenseCategories expense;

    @Schema(description = "收入科目分类")
    private IncomeCategories income;

    @Schema(description = "全部科目列表")
    private List<AccountSubjectDTO> allSubjects;

    /**
     * 支出科目分类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "支出科目分类")
    public static class ExpenseCategories {
        @Schema(description = "发生科目（支出类科目）")
        private List<AccountSubjectDTO> occurrenceSubjects;

        @Schema(description = "付款科目（资产类科目）")
        private List<AccountSubjectDTO> paymentSubjects;
    }

    /**
     * 收入科目分类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "收入科目分类")
    public static class IncomeCategories {
        @Schema(description = "发生科目（收入类科目）")
        private List<AccountSubjectDTO> occurrenceSubjects;

        @Schema(description = "收款科目（资产类科目）")
        private List<AccountSubjectDTO> receiptSubjects;
    }
}
