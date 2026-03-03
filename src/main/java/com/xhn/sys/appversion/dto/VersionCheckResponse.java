package com.xhn.sys.appversion.dto;

import com.xhn.sys.appversion.model.SysAppVersion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 版本检查响应DTO
 *
 * @author xhn
 * @date 2026-03-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "版本检查响应")
public class VersionCheckResponse {

    @Schema(description = "是否有更新")
    private Boolean hasUpdate;

    @Schema(description = "最新版本号")
    private Integer versionCode;

    @Schema(description = "版本名称")
    private String versionName;

    @Schema(description = "文件下载地址")
    private String fileUrl;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文件MD5校验")
    private String fileMd5;

    @Schema(description = "更新日志")
    private String updateLog;

    @Schema(description = "是否强制更新")
    private Integer isForce;

    @Schema(description = "平台类型")
    private String platform;

    /**
     * 从实体转换为响应DTO
     *
     * @param appVersion 应用版本实体
     * @param hasUpdate  是否有更新
     * @return 响应DTO
     */
    public static VersionCheckResponse from(SysAppVersion appVersion, boolean hasUpdate) {
        return VersionCheckResponse.builder()
                .hasUpdate(hasUpdate)
                .versionCode(appVersion.getVersionCode())
                .versionName(appVersion.getVersionName())
                .fileUrl(appVersion.getFileUrl())
                .fileSize(appVersion.getFileSize())
                .fileMd5(appVersion.getFileMd5())
                .updateLog(appVersion.getUpdateLog())
                .isForce(appVersion.getIsForce())
                .platform(appVersion.getPlatform())
                .build();
    }
}
