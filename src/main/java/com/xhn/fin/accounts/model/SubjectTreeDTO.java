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
 * 字段名称与 BaseFinAccounts 实体类保持一致
 *
 * @author xhn
 * @date 2026-02-05
 */
@Setter
@Getter
@Schema(description = "财务科目树形结构")
public class SubjectTreeDTO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "父级ID (根节点为NULL)")
    private Long parentId;

    @Schema(description = "业务编码")
    private String code;

    @Schema(description = "账户名称")
    private String name;

    @Schema(description = "账户类型枚举")
    private AccountType accountTypeEnum;

    @Schema(description = "账户原本的借贷方向枚举")
    private Direction balanceDirectionEnum;

    @Schema(description = "币种代码 (如: CNY, USD)")
    private String currencyCode;

    @Schema(description = "期初余额")
    private BigDecimal initialBalance;

    @Schema(description = "当前余额 (期初 + 累计发生额，计算字段)")
    private BigDecimal currentBalance;

    @Schema(description = "是否归档")
    private Boolean isArchived;

    @Schema(description = "科目说明/备注")
    private String description;

    @Schema(description = "是否叶子节点")
    private Boolean isLeaf;

    @Schema(description = "子节点列表")
    private List<SubjectTreeDTO> children;
}
