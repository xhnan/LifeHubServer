package com.xhn.sys.appversion.model;

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
@TableName("sys_app_version")
@Schema(description = "应用版本")
public class BaseSysAppVersion {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "版本号编码")
    private Integer versionCode;

    @Schema(description = "版本名称")
    private String versionName;

    @Schema(description = "文件存储地址")
    private String fileUrl;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "文件 MD5")
    private String fileMd5;

    @Schema(description = "更新日志")
    private String updateLog;

    @Schema(description = "是否强制更新")
    private Integer isForce;

    @Schema(description = "平台")
    private String platform;

    @Schema(description = "状态：0 禁用，1 启用")
    private Integer status;

    @TableField("published_by_user_id")
    @Schema(description = "发布人用户 ID")
    private Long publishedByUserId;

    @TableField("published_by_api_key_id")
    @Schema(description = "用于发布的 API Key ID")
    private Long publishedByApiKeyId;

    @TableField("publish_source")
    @Schema(description = "发布来源：JWT / USER_API_KEY / LEGACY_API_KEY / UNKNOWN")
    private String publishSource;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @TableLogic
    @Schema(description = "逻辑删除")
    private Boolean isDeleted;
}
