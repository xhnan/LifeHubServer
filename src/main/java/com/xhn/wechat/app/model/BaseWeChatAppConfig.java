package com.xhn.wechat.app.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 企业微信应用配置表实体基类
 * 表名: wechat_app_config
 * @author xhn
 * @date 2026-02-26
 */
@Data
@TableName("wechat_app_config")
@Schema(description = "企业微信应用配置表实体基类")
public class BaseWeChatAppConfig {

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "应用名称")
    @TableField("app_name")
    private String appName;

    @Schema(description = "企业微信企业ID")
    @TableField("corp_id")
    private String corpId;

    @Schema(description = "企业微信应用密钥")
    @TableField("corp_secret")
    private String corpSecret;

    @Schema(description = "企业微信应用AgentId")
    @TableField("agent_id")
    private Long agentId;

    @Schema(description = "回调验证Token")
    @TableField("token")
    private String token;

    @Schema(description = "消息加密密钥")
    @TableField("encoding_aes_key")
    private String encodingAesKey;

    @Schema(description = "是否启用(0=禁用,1=启用)")
    @TableField("is_enabled")
    private Integer isEnabled;

    @Schema(description = "消息处理器Bean名称（完整类名）")
    @TableField("handler_bean_name")
    private String handlerBeanName;

    @Schema(description = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @Schema(description = "创建人ID")
    @TableField("created_by")
    private Long createdBy;
}
