-- 应用版本管理表
CREATE TABLE sys_app_version (
    id              BIGSERIAL PRIMARY KEY,
    version_code    INTEGER NOT NULL,           -- 版本号（整数，如 10001）
    version_name    VARCHAR(20) NOT NULL,       -- 版本名称（如 "1.0.1"）
    file_url        VARCHAR(500) NOT NULL,      -- MinIO文件URL
    file_size       BIGINT,                     -- 文件大小（字节）
    file_md5        VARCHAR(32),                -- 文件MD5校验
    update_log      TEXT,                       -- 更新日志
    is_force        SMALLINT DEFAULT 0,         -- 是否强制更新（0否 1是）
    platform        VARCHAR(20) DEFAULT 'android', -- 平台类型（预留扩展）
    status          SMALLINT DEFAULT 1,         -- 状态（0禁用 1启用）
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted      BOOLEAN DEFAULT FALSE
);

COMMENT ON TABLE sys_app_version IS '应用版本管理表';
COMMENT ON COLUMN sys_app_version.id IS '主键ID';
COMMENT ON COLUMN sys_app_version.version_code IS '版本号（整数）';
COMMENT ON COLUMN sys_app_version.version_name IS '版本名称';
COMMENT ON COLUMN sys_app_version.file_url IS 'MinIO文件URL';
COMMENT ON COLUMN sys_app_version.file_size IS '文件大小（字节）';
COMMENT ON COLUMN sys_app_version.file_md5 IS '文件MD5校验';
COMMENT ON COLUMN sys_app_version.update_log IS '更新日志';
COMMENT ON COLUMN sys_app_version.is_force IS '是否强制更新（0否 1是）';
COMMENT ON COLUMN sys_app_version.platform IS '平台类型';
COMMENT ON COLUMN sys_app_version.status IS '状态（0禁用 1启用）';
COMMENT ON COLUMN sys_app_version.created_at IS '创建时间';
COMMENT ON COLUMN sys_app_version.updated_at IS '更新时间';
COMMENT ON COLUMN sys_app_version.is_deleted IS '是否删除';

CREATE INDEX idx_version_code ON sys_app_version(version_code);
CREATE INDEX idx_platform ON sys_app_version(platform);
CREATE INDEX idx_status ON sys_app_version(status);
