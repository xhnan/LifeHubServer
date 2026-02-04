-- =============================================================================
-- 1. 账户科目表 (核心表)
-- =============================================================================
CREATE TABLE fin_accounts (
                              id              BIGSERIAL PRIMARY KEY,
                              name            VARCHAR(100) NOT NULL,
                              parent_id       BIGINT,
                              account_type    VARCHAR(20) NOT NULL,
                              currency_code   VARCHAR(10) DEFAULT 'CNY',
                              initial_balance NUMERIC(18, 2) DEFAULT 0,
                              is_archived     SMALLINT DEFAULT 0,
                              created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 添加外键约束：父级ID必须指向本表的ID
ALTER TABLE fin_accounts
    ADD CONSTRAINT fk_accounts_parent
        FOREIGN KEY (parent_id) REFERENCES fin_accounts (id);

-- 添加注释
COMMENT ON TABLE fin_accounts IS '账户科目表：存储资产、负债、收入、支出等所有节点';
COMMENT ON COLUMN fin_accounts.id IS '主键 ID';
COMMENT ON COLUMN fin_accounts.name IS '账户名称 (如：招商银行、午餐)';
COMMENT ON COLUMN fin_accounts.parent_id IS '父级 ID (实现树形结构，根节点为 NULL)';
COMMENT ON COLUMN fin_accounts.account_type IS '账户类型 (枚举值: ASSET, LIABILITY, EQUITY, INCOME, EXPENSE)';
COMMENT ON COLUMN fin_accounts.currency_code IS '币种代码 (如: CNY, USD)';
COMMENT ON COLUMN fin_accounts.initial_balance IS '期初余额 (用于系统初始化时的存量)';
COMMENT ON COLUMN fin_accounts.is_archived IS '是否归档 (0:启用, 1:归档/逻辑删除)';

-- =============================================================================
-- 2. 交易主表 (Header)
-- =============================================================================
CREATE TABLE fin_transactions (
                                  id              BIGSERIAL PRIMARY KEY,
                                  trans_date      TIMESTAMP NOT NULL,
                                  description     VARCHAR(500),
                                  attachment_id   VARCHAR(100),
                                  created_by      BIGINT,
                                  created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 添加注释
COMMENT ON TABLE fin_transactions IS '交易主表：记录交易发生的时间、描述等元数据';
COMMENT ON COLUMN fin_transactions.id IS '主键 ID';
COMMENT ON COLUMN fin_transactions.trans_date IS '交易发生的实际时间';
COMMENT ON COLUMN fin_transactions.description IS '交易描述 (如：周末超市采购)';
COMMENT ON COLUMN fin_transactions.attachment_id IS '附件 ID (关联 MinIO 文件对象)';
COMMENT ON COLUMN fin_transactions.created_by IS '创建人 ID (关联 RBAC 用户表)';

-- =============================================================================
-- 3. 交易分录表 (Detail - 复式记账核心)
-- =============================================================================
CREATE TABLE fin_entries (
                             id              BIGSERIAL PRIMARY KEY,
                             trans_id        BIGINT NOT NULL,
                             account_id      BIGINT NOT NULL,
                             direction       SMALLINT NOT NULL,
                             amount          NUMERIC(18, 2) NOT NULL,
                             quantity        NUMERIC(18, 6),
                             price           NUMERIC(18, 6),
                             commodity_code  VARCHAR(20)
);

-- 添加外键约束
ALTER TABLE fin_entries
    ADD CONSTRAINT fk_entries_trans
        FOREIGN KEY (trans_id) REFERENCES fin_transactions (id) ON DELETE CASCADE;

ALTER TABLE fin_entries
    ADD CONSTRAINT fk_entries_account
        FOREIGN KEY (account_id) REFERENCES fin_accounts (id);

-- 添加索引 (加速查询)
CREATE INDEX idx_entries_trans_id ON fin_entries(trans_id);
CREATE INDEX idx_entries_account_id ON fin_entries(account_id);

-- 添加注释
COMMENT ON TABLE fin_entries IS '交易分录表：复式记账的具体资金流向';
COMMENT ON COLUMN fin_entries.id IS '主键 ID';
COMMENT ON COLUMN fin_entries.trans_id IS '关联交易主表 ID';
COMMENT ON COLUMN fin_entries.account_id IS '关联账户表 ID';
COMMENT ON COLUMN fin_entries.direction IS '借贷方向 (1:借/Debit, -1:贷/Credit)';
COMMENT ON COLUMN fin_entries.amount IS '发生金额 (本币金额，用于报表统计)';
COMMENT ON COLUMN fin_entries.quantity IS '数量 (投资专用：如 100 股, 0.5 克黄金)';
COMMENT ON COLUMN fin_entries.price IS '单价 (投资专用：记录交易时的成交单价)';
COMMENT ON COLUMN fin_entries.commodity_code IS '标的物代码 (如 TENCENT, USD，为空则默认本币)';

-- =============================================================================
-- 4. 预算表 (风控)
-- =============================================================================
CREATE TABLE fin_budgets (
                             id              BIGSERIAL PRIMARY KEY,
                             account_id      BIGINT NOT NULL,
                             amount          NUMERIC(18, 2) NOT NULL,
                             period_type     VARCHAR(10) NOT NULL,
                             period_date     DATE NOT NULL,
                             user_id         BIGINT,
                             created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE fin_budgets
    ADD CONSTRAINT fk_budgets_account
        FOREIGN KEY (account_id) REFERENCES fin_accounts (id);

-- 添加注释
COMMENT ON TABLE fin_budgets IS '预算表：设定特定周期的支出限额';
COMMENT ON COLUMN fin_budgets.id IS '主键 ID';
COMMENT ON COLUMN fin_budgets.account_id IS '关联的账户科目 ID';
COMMENT ON COLUMN fin_budgets.amount IS '预算限额';
COMMENT ON COLUMN fin_budgets.period_type IS '预算周期类型 (MONTHLY, YEARLY)';
COMMENT ON COLUMN fin_budgets.period_date IS '预算所属日期 (如 2026-02-01 代表2月)';
COMMENT ON COLUMN fin_budgets.user_id IS '关联用户 ID (支持多用户独立预算)';

-- =============================================================================
-- 5. 历史价格表 (投资估值)
-- =============================================================================
CREATE TABLE fin_prices (
                            id              BIGSERIAL PRIMARY KEY,
                            commodity_code  VARCHAR(20) NOT NULL,
                            price_date      TIMESTAMP NOT NULL,
                            close_price     NUMERIC(18, 6) NOT NULL,
                            created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 添加联合唯一索引，防止同一天同一标的重复插入
CREATE UNIQUE INDEX idx_prices_unique ON fin_prices(commodity_code, price_date);

-- 添加注释
COMMENT ON TABLE fin_prices IS '行情价格表：存储股票/基金/外汇的历史收盘价';
COMMENT ON COLUMN fin_prices.commodity_code IS '标的物代码 (如 00700.HK)';
COMMENT ON COLUMN fin_prices.price_date IS '价格对应的时间点';
COMMENT ON COLUMN fin_prices.close_price IS '收盘价/汇率';

-- =============================================================================
-- 6. 标签表 (多维分析)
-- =============================================================================
CREATE TABLE fin_tags (
                          id              BIGSERIAL PRIMARY KEY,
                          tag_name        VARCHAR(50) NOT NULL,
                          created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE fin_tags IS '标签定义表：用于自定义业务标签';
COMMENT ON COLUMN fin_tags.tag_name IS '标签名称 (如：出差、装修、宠物)';

-- =============================================================================
-- 7. 交易-标签关联表 (多对多)
-- =============================================================================
CREATE TABLE fin_trans_tags (
                                trans_id        BIGINT NOT NULL,
                                tag_id          BIGINT NOT NULL,
                                PRIMARY KEY (trans_id, tag_id)
);

ALTER TABLE fin_trans_tags
    ADD CONSTRAINT fk_tt_trans
        FOREIGN KEY (trans_id) REFERENCES fin_transactions (id) ON DELETE CASCADE;

ALTER TABLE fin_trans_tags
    ADD CONSTRAINT fk_tt_tag
        FOREIGN KEY (tag_id) REFERENCES fin_tags (id) ON DELETE CASCADE;

COMMENT ON TABLE fin_trans_tags IS '交易与标签的关联表 (多对多)';

-- 补充
ALTER TABLE fin_accounts
    ADD COLUMN description VARCHAR(500);

COMMENT ON COLUMN fin_accounts.description IS '科目说明/备注：用于解释该科目的核算范围';


ALTER TABLE fin_accounts
    ADD COLUMN is_leaf BOOLEAN DEFAULT TRUE; -- 默认为 true，因为新建的科目默认没有子节点

COMMENT ON COLUMN fin_accounts.is_leaf IS '是否叶子节点：只有 true 的科目允许被录入凭证';

ALTER TABLE fin_accounts
    ADD COLUMN code VARCHAR(50);