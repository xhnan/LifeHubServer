package com.xhn.fin.accounts.model;

import com.xhn.fin.enums.AccountType;
import com.xhn.fin.enums.Direction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 财务科目树形结构 DTO
 * 用于返回树形结构的科目数据
 *
 * @author xhn
 * @date 2026-02-05
 */
@Setter
@Getter
@Schema(description = "财务科目树形结构")
public class SubjectTreeDTO {

    @Schema(description = "科目ID")
    private Long id;

    @Schema(description = "父科目ID (0表示根节点)")
    private Long parentId;

    @Schema(description = "科目编码")
    private String subjectCode;

    @Schema(description = "科目名称")
    private String subjectName;

    @Schema(description = "科目类型 (ASSET/LIABILITY/EQUITY/INCOME/EXPENSE)")
    private AccountType subjectType;

    @Schema(description = "借贷方向 (DEBIT/CREDIT)")
    private Direction direction;

    @Schema(description = "币种代码 (如: CNY, USD)")
    private String currencyCode;

    @Schema(description = "期初余额")
    private BigDecimal initialBalance;

    @Schema(description = "当前余额 (期初 + 累计发生额)")
    private BigDecimal currentBalance;

    @Schema(description = "状态 (false:启用, true:归档)")
    private Boolean isArchived;

    @Schema(description = "备注说明")
    private String description;

    @Schema(description = "是否叶子节点")
    private Boolean isLeaf;

    @Schema(description = "子节点列表")
    private List<SubjectTreeDTO> children;
}
