-- 企业微信内部应用集成数据库表
-- Create Date: 2026-02-26

-- 企业微信应用配置表
CREATE TABLE IF NOT EXISTS wechat_app_config (
    id              BIGSERIAL PRIMARY KEY,
    app_name        VARCHAR(100) NOT NULL,
    corp_id         VARCHAR(100) NOT NULL,
    corp_secret     VARCHAR(200) NOT NULL,
    agent_id        BIGINT NOT NULL,
    token           VARCHAR(200),
    encoding_aes_key VARCHAR(300),
    is_enabled      SMALLINT DEFAULT 1,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by      BIGINT
);

COMMENT ON TABLE wechat_app_config IS '企业微信应用配置表';
COMMENT ON COLUMN wechat_app_config.id IS '主键ID';
COMMENT ON COLUMN wechat_app_config.app_name IS '应用名称';
COMMENT ON COLUMN wechat_app_config.corp_id IS '企业微信企业ID';
COMMENT ON COLUMN wechat_app_config.corp_secret IS '企业微信应用密钥';
COMMENT ON COLUMN wechat_app_config.agent_id IS '企业微信应用AgentId';
COMMENT ON COLUMN wechat_app_config.token IS '回调验证Token';
COMMENT ON COLUMN wechat_app_config.encoding_aes_key IS '消息加密密钥';
COMMENT ON COLUMN wechat_app_config.is_enabled IS '是否启用(0=禁用,1=启用)';
COMMENT ON COLUMN wechat_app_config.created_at IS '创建时间';
COMMENT ON COLUMN wechat_app_config.updated_at IS '更新时间';
COMMENT ON COLUMN wechat_app_config.created_by IS '创建人ID';

-- 企业微信消息记录表
CREATE TABLE IF NOT EXISTS wechat_message (
    id              BIGSERIAL PRIMARY KEY,
    app_id          BIGINT NOT NULL,
    msg_direction   VARCHAR(20) NOT NULL,
    msg_type        VARCHAR(50) NOT NULL,
    from_user       VARCHAR(100),
    to_user         VARCHAR(100),
    content         TEXT,
    msg_id          VARCHAR(100),
    status          VARCHAR(20),
    error_code      VARCHAR(20),
    error_msg       VARCHAR(500),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE wechat_message IS '企业微信消息记录表';
COMMENT ON COLUMN wechat_message.id IS '主键ID';
COMMENT ON COLUMN wechat_message.app_id IS '应用ID(关联wechat_app_config.id)';
COMMENT ON COLUMN wechat_message.msg_direction IS '消息方向(inbound=接收,outbound=发送)';
COMMENT ON COLUMN wechat_message.msg_type IS '消息类型(text,image,voice,video,file,event等)';
COMMENT ON COLUMN wechat_message.from_user IS '发送方UserID';
COMMENT ON COLUMN wechat_message.to_user IS '接收方UserID';
COMMENT ON COLUMN wechat_message.content IS '消息内容';
COMMENT ON COLUMN wechat_message.msg_id IS '消息ID';
COMMENT ON COLUMN wechat_message.status IS '消息状态(success,failed,pending)';
COMMENT ON COLUMN wechat_message.error_code IS '错误码';
COMMENT ON COLUMN wechat_message.error_msg IS '错误信息';
COMMENT ON COLUMN wechat_message.created_at IS '创建时间';

-- 企业微信用户绑定表
CREATE TABLE IF NOT EXISTS wechat_user_binding (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    wx_user_id      VARCHAR(100) NOT NULL,
    app_id          BIGINT NOT NULL,
    wx_openid       VARCHAR(100),
    wx_name         VARCHAR(100),
    wx_department   VARCHAR(500),
    wx_position     VARCHAR(100),
    wx_mobile       VARCHAR(50),
    wx_email        VARCHAR(100),
    wx_status       SMALLINT DEFAULT 1,
    is_primary      SMALLINT DEFAULT 1,
    bind_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_sync_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE wechat_user_binding IS '企业微信用户绑定表';
COMMENT ON COLUMN wechat_user_binding.id IS '主键ID';
COMMENT ON COLUMN wechat_user_binding.user_id IS '系统用户ID(关联sys_user.id)';
COMMENT ON COLUMN wechat_user_binding.wx_user_id IS '企业微信UserID';
COMMENT ON COLUMN wechat_user_binding.app_id IS '应用ID(关联wechat_app_config.id)';
COMMENT ON COLUMN wechat_user_binding.wx_openid IS '企业微信OpenID';
COMMENT ON COLUMN wechat_user_binding.wx_name IS '企业微信用户姓名';
COMMENT ON COLUMN wechat_user_binding.wx_department IS '所属部门';
COMMENT ON COLUMN wechat_user_binding.wx_position IS '职位';
COMMENT ON COLUMN wechat_user_binding.wx_mobile IS '手机号';
COMMENT ON COLUMN wechat_user_binding.wx_email IS '邮箱';
COMMENT ON COLUMN wechat_user_binding.wx_status IS '激活状态(0=已停用,1=已激活,2=未激活,4=被禁用)';
COMMENT ON COLUMN wechat_user_binding.is_primary IS '是否为主账号(0=否,1=是)';
COMMENT ON COLUMN wechat_user_binding.bind_time IS '绑定时间';
COMMENT ON COLUMN wechat_user_binding.last_sync_time IS '最后同步时间';
COMMENT ON COLUMN wechat_user_binding.created_at IS '创建时间';
COMMENT ON COLUMN wechat_user_binding.updated_at IS '更新时间';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_wechat_app_config_agent_id ON wechat_app_config(agent_id);
CREATE INDEX IF NOT EXISTS idx_wechat_app_config_is_enabled ON wechat_app_config(is_enabled);

CREATE INDEX IF NOT EXISTS idx_wechat_message_app_id ON wechat_message(app_id);
CREATE INDEX IF NOT EXISTS idx_wechat_message_from_user ON wechat_message(from_user);
CREATE INDEX IF NOT EXISTS idx_wechat_message_created_at ON wechat_message(created_at);

CREATE INDEX IF NOT EXISTS idx_wechat_user_binding_user_id ON wechat_user_binding(user_id);
CREATE INDEX IF NOT EXISTS idx_wechat_user_binding_wx_user_id ON wechat_user_binding(wx_user_id);
CREATE INDEX IF NOT EXISTS idx_wechat_user_binding_app_id ON wechat_user_binding(app_id);
CREATE INDEX IF NOT EXISTS idx_wechat_user_binding_union ON wechat_user_binding(user_id, app_id);
