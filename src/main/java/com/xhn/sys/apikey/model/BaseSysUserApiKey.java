package com.xhn.sys.apikey.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user_api_key")
@Schema(description = "用户 API Key")
public class BaseSysUserApiKey {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键 ID")
    private Long id;

    @TableField("user_id")
    @Schema(description = "归属用户 ID")
    private Long userId;

    @TableField("key_name")
    @Schema(description = "Key 名称")
    private String keyName;

    @TableField("key_prefix")
    @Schema(description = "Key 前缀，用于展示和快速定位")
    private String keyPrefix;

    @TableField("api_key_hash")
    @Schema(description = "API Key 哈希值")
    private String apiKeyHash;

    @TableField("description")
    @Schema(description = "用途说明")
    private String description;

    @TableField("allowed_paths")
    @Schema(description = "允许访问的接口路径范围，逗号分隔")
    private String allowedPaths;

    @TableField("status")
    @Schema(description = "状态：active / revoked / expired")
    private String status;

    @TableField("last_used_at")
    @Schema(description = "最后使用时间")
    private LocalDateTime lastUsedAt;

    @TableField("expires_at")
    @Schema(description = "过期时间，为空表示不过期")
    private LocalDateTime expiresAt;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @TableField("is_deleted")
    @TableLogic
    @Schema(description = "逻辑删除")
    private Boolean isDeleted;
}
