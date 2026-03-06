package com.xhn.fin.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 科目数据传输对象
 * 用于返回科目的基本信息
 *
 * @author xhn
 * @date 2026-03-05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "科目信息")
public class AccountSubjectDTO {

    @Schema(description = "科目ID")
    private Long id;

    @Schema(description = "科目名称")
    private String name;

    @Schema(description = "科目代码")
    private String code;

    @Schema(description = "账户类型")
    private String accountType;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序权重")
    private Integer sortWeight;
}
