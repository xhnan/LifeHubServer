package com.xhn.fin.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 账户科目轻量 DTO
 * 用于前端选择器中的科目列表展示。
 *
 * @author xhn
 * @date 2026-03-05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "账户科目 DTO")
public class AccountSubjectDTO {

    @Schema(description = "账户科目 ID")
    private Long id;

    @Schema(description = "账户科目名称")
    private String name;

    @Schema(description = "账户科目编码")
    private String code;

    @Schema(description = "账户类型")
    private String accountType;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "默认排序，正序")
    private Long sortOrder;

    @Schema(description = "用户设置排序，倒序")
    private Integer sortWeight;

    @Schema(description = "后端计算后的最终排序，前端按正序使用")
    private Integer userSort;
}
