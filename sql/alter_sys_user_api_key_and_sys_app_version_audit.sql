ALTER TABLE sys_user_api_key
    ADD COLUMN IF NOT EXISTS allowed_paths TEXT NOT NULL DEFAULT '/sys/app-version/quick-publish';

COMMENT ON COLUMN sys_user_api_key.allowed_paths IS '允许访问的接口路径范围，逗号分隔';

ALTER TABLE sys_app_version
    ADD COLUMN IF NOT EXISTS published_by_user_id BIGINT,
    ADD COLUMN IF NOT EXISTS published_by_api_key_id BIGINT,
    ADD COLUMN IF NOT EXISTS publish_source VARCHAR(32);

COMMENT ON COLUMN sys_app_version.published_by_user_id IS '发布人用户 ID';
COMMENT ON COLUMN sys_app_version.published_by_api_key_id IS '调用发布接口时使用的 API Key ID';
COMMENT ON COLUMN sys_app_version.publish_source IS '发布来源：JWT / USER_API_KEY / LEGACY_API_KEY / UNKNOWN';

CREATE INDEX IF NOT EXISTS idx_sys_app_version_published_by_user_id
    ON sys_app_version(published_by_user_id);
