package com.xhn.fin.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "批量更新科目排序权重请求")
public class SortWeightUpdateDTO {

    @NotNull(message = "账本ID不能为空")
    @Schema(description = "账本ID")
    private Long bookId;

    @NotEmpty(message = "排序项不能为空")
    @Schema(description = "排序项列表")
    private List<SortItem> items;

    @Data
    @Schema(description = "单个科目排序项")
    public static class SortItem {

        @NotNull(message = "科目ID不能为空")
        @Schema(description = "科目ID")
        private Long id;

        @Schema(description = "排序权重（倒序，值越大越靠前，>=1000为置顶）")
        private Integer sortWeight;
    }
}
