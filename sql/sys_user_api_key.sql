CREATE TABLE sys_user_api_key (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    key_name        VARCHAR(100) NOT NULL,
    key_prefix      VARCHAR(32) NOT NULL,
    api_key_hash    VARCHAR(64) NOT NULL,
    description     VARCHAR(500),
    allowed_paths   TEXT NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'active',
    last_used_at    TIMESTAMP,
    expires_at      TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted      BOOLEAN NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE sys_user_api_key IS '用户自定义 API Key';
COMMENT ON COLUMN sys_user_api_key.user_id IS '归属用户 ID';
COMMENT ON COLUMN sys_user_api_key.key_name IS 'API Key 名称';
COMMENT ON COLUMN sys_user_api_key.key_prefix IS 'API Key 前缀，用于脱敏展示';
COMMENT ON COLUMN sys_user_api_key.api_key_hash IS 'API Key 哈希值，数据库不存明文';
COMMENT ON COLUMN sys_user_api_key.description IS '用途说明';
COMMENT ON COLUMN sys_user_api_key.allowed_paths IS '允许访问的接口路径范围，逗号分隔';
COMMENT ON COLUMN sys_user_api_key.status IS '状态：active / revoked / expired';
COMMENT ON COLUMN sys_user_api_key.last_used_at IS '最后使用时间';
COMMENT ON COLUMN sys_user_api_key.expires_at IS '过期时间';
COMMENT ON COLUMN sys_user_api_key.is_deleted IS '逻辑删除';

CREATE INDEX idx_sys_user_api_key_user_id ON sys_user_api_key(user_id);
CREATE UNIQUE INDEX uk_sys_user_api_key_hash ON sys_user_api_key(api_key_hash);
CREATE INDEX idx_sys_user_api_key_status ON sys_user_api_key(status);
