package com.xhn.wechat.binding.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 企业微信用户绑定表实体基类
 * 表名: wechat_user_binding
 * @author xhn
 * @date 2026-02-26
 */
@Data
@TableName("wechat_user_binding")
@Schema(description = "企业微信用户绑定表实体基类")
public class BaseWeChatUserBinding {

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "系统用户ID(关联sys_user.user_id)")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "企业微信UserID")
    @TableField("wx_user_id")
    private String wxUserId;

    @Schema(description = "应用ID(关联wechat_app_config.id)")
    @TableField("app_id")
    private Long appId;

    @Schema(description = "企业微信OpenID")
    @TableField("wx_openid")
    private String wxOpenid;

    @Schema(description = "企业微信用户姓名")
    @TableField("wx_name")
    private String wxName;

    @Schema(description = "所属部门")
    @TableField("wx_department")
    private String wxDepartment;

    @Schema(description = "职位")
    @TableField("wx_position")
    private String wxPosition;

    @Schema(description = "手机号")
    @TableField("wx_mobile")
    private String wxMobile;

    @Schema(description = "邮箱")
    @TableField("wx_email")
    private String wxEmail;

    @Schema(description = "激活状态(0=已停用,1=已激活,2=未激活,4=被禁用)")
    @TableField("wx_status")
    private Integer wxStatus;

    @Schema(description = "是否为主账号(0=否,1=是)")
    @TableField("is_primary")
    private Integer isPrimary;

    @Schema(description = "绑定时间")
    @TableField("bind_time")
    private LocalDateTime bindTime;

    @Schema(description = "最后同步时间")
    @TableField("last_sync_time")
    private LocalDateTime lastSyncTime;

    @Schema(description = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
