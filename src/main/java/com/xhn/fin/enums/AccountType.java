package com.xhn.fin.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 账户类型枚举
 * 对应财务科目表的 account_type 字段
 * @author xhn
 * @date 2026-02-04
 */
public enum AccountType {

    /**
     * 资产类账户
     */
    ASSET("ASSET", "资产"),

    /**
     * 负债类账户
     */
    LIABILITY("LIABILITY", "负债"),

    /**
     * 权益类账户
     */
    EQUITY("EQUITY", "权益"),

    /**
     * 收入类账户
     */
    INCOME("INCOME", "收入"),

    /**
     * 支出类账户
     */
    EXPENSE("EXPENSE", "支出");

    /**
     * 存储到数据库的枚举值
     */
    @EnumValue
    private final String code;

    /**
     * 枚举显示值
     */
    @JsonValue
    private final String description;

    AccountType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取枚举
     * @param code 枚举代码
     * @return 对应的枚举值
     */
    public static AccountType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (AccountType type : AccountType.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown account type code: " + code);
    }
}
