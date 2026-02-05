package com.xhn.fin.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 借贷方向枚举
 * 对应财务科目表的 direction 字段
 *
 * @author xhn
 * @date 2026-02-05
 */
public enum Direction {

    /**
     * 借方
     */
    DEBIT("DEBIT", "借"),

    /**
     * 贷方
     */
    CREDIT("CREDIT", "贷");

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

    Direction(String code, String description) {
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
     *
     * @param code 枚举代码
     * @return 对应的枚举值，未找到返回 null
     */
    public static Direction fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (Direction direction : Direction.values()) {
            if (direction.code.equals(code)) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Unknown direction code: " + code);
    }
}
