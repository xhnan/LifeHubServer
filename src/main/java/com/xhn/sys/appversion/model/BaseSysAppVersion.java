package com.xhn.sys.appversion.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 应用版本管理表 基础实体类
 *
 * @author xhn
 * @date 2026-03-02
 */
@Data
@TableName("sys_app_version")
@Schema(description = "应用版本管理表")
public class BaseSysAppVersion {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "版本号（整数）")
    private Integer versionCode;

    @Schema(description = "版本名称")
    private String versionName;

    @Schema(description = "MinIO文件URL")
    private String fileUrl;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文件MD5校验")
    private String fileMd5;

    @Schema(description = "更新日志")
    private String updateLog;

    @Schema(description = "是否强制更新（0否 1是）")
    private Integer isForce;

    @Schema(description = "平台类型")
    private String platform;

    @Schema(description = "状态（0禁用 1启用）")
    private Integer status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @TableLogic
    @Schema(description = "是否删除")
    private Boolean isDeleted;
}
