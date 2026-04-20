CREATE TABLE sys_app_version (
    id                      BIGSERIAL PRIMARY KEY,
    version_code            INTEGER NOT NULL,
    version_name            VARCHAR(20) NOT NULL,
    file_url                VARCHAR(500) NOT NULL,
    file_size               BIGINT,
    file_md5                VARCHAR(32),
    update_log              TEXT,
    is_force                SMALLINT DEFAULT 0,
    platform                VARCHAR(20) DEFAULT 'android',
    status                  SMALLINT DEFAULT 1,
    published_by_user_id    BIGINT,
    published_by_api_key_id BIGINT,
    publish_source          VARCHAR(32),
    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted              BOOLEAN DEFAULT FALSE
);

COMMENT ON TABLE sys_app_version IS '应用版本表';
COMMENT ON COLUMN sys_app_version.id IS '主键 ID';
COMMENT ON COLUMN sys_app_version.version_code IS '版本编码';
COMMENT ON COLUMN sys_app_version.version_name IS '版本名称';
COMMENT ON COLUMN sys_app_version.file_url IS '文件地址';
COMMENT ON COLUMN sys_app_version.file_size IS '文件大小';
COMMENT ON COLUMN sys_app_version.file_md5 IS '文件 MD5';
COMMENT ON COLUMN sys_app_version.update_log IS '更新日志';
COMMENT ON COLUMN sys_app_version.is_force IS '是否强制更新';
COMMENT ON COLUMN sys_app_version.platform IS '平台';
COMMENT ON COLUMN sys_app_version.status IS '状态：0 禁用，1 启用';
COMMENT ON COLUMN sys_app_version.published_by_user_id IS '发布人用户 ID';
COMMENT ON COLUMN sys_app_version.published_by_api_key_id IS '调用发布接口时使用的 API Key ID';
COMMENT ON COLUMN sys_app_version.publish_source IS '发布来源：JWT / USER_API_KEY / LEGACY_API_KEY / UNKNOWN';
COMMENT ON COLUMN sys_app_version.created_at IS '创建时间';
COMMENT ON COLUMN sys_app_version.updated_at IS '更新时间';
COMMENT ON COLUMN sys_app_version.is_deleted IS '逻辑删除';

CREATE INDEX idx_version_code ON sys_app_version(version_code);
CREATE INDEX idx_platform ON sys_app_version(platform);
CREATE INDEX idx_status ON sys_app_version(status);
CREATE INDEX idx_sys_app_version_published_by_user_id ON sys_app_version(published_by_user_id);
