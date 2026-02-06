package com.xhn.fin.accounts.model;

import com.xhn.fin.enums.AccountType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 账户表实体类
 * 继承自 BaseFinAccounts，用于业务逻辑扩展
 *
 * 使用示例：
 * <pre>
 * FinAccounts account = new FinAccounts();
 * account.setAccountTypeEnum(AccountType.ASSET);  // 设置枚举
 * AccountType type = account.getAccountTypeEnum();  // 获取枚举
 * </pre>
 *
 * @author xhn
 * @date 2026-02-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FinAccounts extends BaseFinAccounts {

    /**
     * 【推荐使用】强类型 Getter - 获取账户类型枚举
     * 业务代码里优先调用这个方法，而不是父类的 getAccountType()
     *
     * @return 账户类型枚举，如果数据库值为 null 则返回 null
     */
    public AccountType getAccountTypeEnum() {
        String code = super.getAccountType();
        return AccountType.fromCode(code);
    }

    /**
     * 【推荐使用】强类型 Setter - 设置账户类型枚举
     * 业务代码里优先调用这个方法，而不是父类的 setAccountType(String)
     *
     * @param type 账户类型枚举，传 null 会清空字段
     */
    public void setAccountTypeEnum(AccountType type) {
        if (type != null) {
            // 将枚举的 code 存入数据库（如 "ASSET"）
            super.setAccountType(type.getCode());
        } else {
            super.setAccountType(null);
        }
    }

    /**
     * 判断是否为资产类账户
     */
    public boolean isAsset() {
        return AccountType.ASSET.equals(getAccountTypeEnum());
    }

    /**
     * 判断是否为负债类账户
     */
    public boolean isLiability() {
        return AccountType.LIABILITY.equals(getAccountTypeEnum());
    }

    /**
     * 判断是否为权益类账户
     */
    public boolean isEquity() {
        return AccountType.EQUITY.equals(getAccountTypeEnum());
    }

    /**
     * 判断是否为收入类账户
     */
    public boolean isIncome() {
        return AccountType.INCOME.equals(getAccountTypeEnum());
    }

    /**
     * 判断是否为支出类账户
     */
    public boolean isExpense() {
        return AccountType.EXPENSE.equals(getAccountTypeEnum());
    }

    /**
     * 【推荐使用】强类型 Getter - 获取借贷方向枚举
     * 业务代码里优先调用这个方法，而不是父类的 getBalanceDirection()
     *
     * @return 借贷方向枚举，如果数据库值为 null 则返回 null
     */
    public com.xhn.fin.enums.Direction getBalanceDirectionEnum() {
        String code = super.getBalanceDirection();
        if (code == null) {
            return null;
        }
        return com.xhn.fin.enums.Direction.fromCode(code);
    }

    /**
     * 【推荐使用】强类型 Setter - 设置借贷方向枚举
     * 业务代码里优先调用这个方法，而不是父类的 setBalanceDirection(String)
     *
     * @param direction 借贷方向枚举，传 null 会清空字段
     */
    public void setBalanceDirectionEnum(com.xhn.fin.enums.Direction direction) {
        if (direction != null) {
            super.setBalanceDirection(direction.getCode());
        } else {
            super.setBalanceDirection(null);
        }
    }

}