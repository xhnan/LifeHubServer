package com.xhn.sys.appversion.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.base.utils.MinioUtil;
import com.xhn.response.ResponseResult;
import com.xhn.sys.appversion.dto.VersionCheckResponse;
import com.xhn.sys.appversion.model.SysAppVersion;
import com.xhn.sys.appversion.service.SysAppVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * 应用版本管理控制器
 * <p>
 * 管理端路径: /sys/app-version
 * 客户端路径: /app/app-version
 *
 * @author xhn
 * @date 2026-03-02
 */
@Slf4j
@RestController
@RequestMapping("/sys/app-version")
@Tag(name = "应用版本管理", description = "应用版本管理和更新检查API")
public class SysAppVersionController {

    @Autowired
    private SysAppVersionService sysAppVersionService;

    @Autowired
    private MinioUtil minioUtil;

    @Value("${minio.bucket-name:lifehub}")
    private String bucketName;

    // ==================== 管理端API (/sys/app-version) ====================

    /**
     * 快速上传并发布新版本（自动禁用旧版本）
     */
    @PostMapping(value = "/quick-publish", consumes = "multipart/form-data")
    @Operation(summary = "快速发布新版本", description = "上传APK文件并自动禁用旧版本，新版本自动启用")
    public Mono<ResponseResult<SysAppVersion>> quickPublish(
            @Parameter(description = "版本号") @RequestPart("versionCode") FormFieldPart versionCodePart,
            @Parameter(description = "版本名称") @RequestPart("versionName") FormFieldPart versionNamePart,
            @Parameter(description = "APK文件") @RequestPart("file") FilePart file,
            @Parameter(description = "更新日志") @RequestPart(value = "updateLog", required = false) FormFieldPart updateLogPart,
            @Parameter(description = "是否强制更新") @RequestPart(value = "isForce", required = false) FormFieldPart isForcePart,
            @Parameter(description = "平台类型") @RequestPart(value = "platform", required = false) FormFieldPart platformPart) {

        // 从 FormFieldPart 提取字符串值
        String versionCode = versionCodePart.value();
        String versionName = versionNamePart.value();
        String updateLog = updateLogPart != null ? updateLogPart.value() : null;
        String isForce = isForcePart != null ? isForcePart.value() : "0";
        String platform = platformPart != null ? platformPart.value() : "android";

        log.info("收到版本号: {}, 文件名: {}", versionCode, file.filename());

        return sysAppVersionService.quickPublish(
                    Integer.valueOf(versionCode), versionName, file, updateLog,
                    Integer.valueOf(isForce), platform)
                .map(result -> ResponseResult.success(result))
                .onErrorResume(e -> {
                    log.error("Quick publish error: {}", e.getMessage(), e);
                    return Mono.just(ResponseResult.error("快速发布版本失败: " + e.getMessage()));
                });
    }

    /**
     * 发布新版本（上传APK到MinIO）
     */
    @PostMapping(value = "/publish", consumes = "multipart/form-data")
    @Operation(summary = "发布新版本", description = "上传APK文件并发布新版本")
    public Mono<ResponseResult<SysAppVersion>> publishVersion(
            @Parameter(description = "版本号") @RequestPart("versionCode") FormFieldPart versionCodePart,
            @Parameter(description = "版本名称") @RequestPart("versionName") FormFieldPart versionNamePart,
            @Parameter(description = "APK文件") @RequestPart("file") FilePart file,
            @Parameter(description = "更新日志") @RequestPart(value = "updateLog", required = false) FormFieldPart updateLogPart,
            @Parameter(description = "是否强制更新") @RequestPart(value = "isForce", required = false) FormFieldPart isForcePart,
            @Parameter(description = "平台类型") @RequestPart(value = "platform", required = false) FormFieldPart platformPart) {

        // 从 FormFieldPart 提取字符串值
        String versionCode = versionCodePart.value();
        String versionName = versionNamePart.value();
        String updateLog = updateLogPart != null ? updateLogPart.value() : null;
        String isForce = isForcePart != null ? isForcePart.value() : "0";
        String platform = platformPart != null ? platformPart.value() : "android";

        log.info("收到版本号: {}, 文件名: {}", versionCode, file.filename());

        return sysAppVersionService.publishVersion(
                    Integer.valueOf(versionCode), versionName, file, updateLog,
                    Integer.valueOf(isForce), platform)
                .map(result -> ResponseResult.success(result))
                .onErrorResume(e -> {
                    log.error("Publish version error: {}", e.getMessage(), e);
                    return Mono.just(ResponseResult.error("发布版本失败: " + e.getMessage()));
                });
    }

    /**
     * 删除版本
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除版本", description = "根据ID删除版本")
    public Mono<ResponseResult<Boolean>> deleteVersion(@PathVariable Long id) {
        return Mono.fromCallable(() -> sysAppVersionService.removeById(id))
                .map(result -> result ? ResponseResult.success(true) : ResponseResult.error("删除失败"));
    }

    /**
     * 更新版本信息
     */
    @PutMapping("")
    @Operation(summary = "更新版本", description = "更新版本信息（不包括文件）")
    public Mono<ResponseResult<Boolean>> updateVersion(@RequestBody SysAppVersion appVersion) {
        return Mono.fromCallable(() -> sysAppVersionService.updateById(appVersion))
                .map(result -> result ? ResponseResult.success(true) : ResponseResult.error("更新失败"));
    }

    /**
     * 查询版本详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询版本详情", description = "根据ID查询版本详情")
    public Mono<ResponseResult<SysAppVersion>> getVersionById(@PathVariable Long id) {
        return Mono.fromCallable(() -> sysAppVersionService.getById(id))
                .map(appVersion -> {
                    if (appVersion != null) {
                        // 生成临时下载URL
                        appVersion.setFileUrl(generatePresignedUrl(appVersion.getFileUrl()));
                        return ResponseResult.success(appVersion);
                    }
                    return ResponseResult.<SysAppVersion>error("查询失败");
                });
    }

    /**
     * 分页查询版本列表
     */
    @GetMapping("")
    @Operation(summary = "查询版本列表", description = "分页查询版本列表")
    public Mono<ResponseResult<Page<SysAppVersion>>> listVersions(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "平台类型") @RequestParam(required = false) String platform) {
        return Mono.fromCallable(() -> {
            Page<SysAppVersion> page = new Page<>(current, size);
            Page<SysAppVersion> result = (Page<SysAppVersion>) sysAppVersionService.pageList(page, platform);
            // 为每个版本生成临时下载URL
            result.getRecords().forEach(version ->
                version.setFileUrl(generatePresignedUrl(version.getFileUrl()))
            );
            return result;
        }).map(ResponseResult::success);
    }

    /**
     * 获取最新版本
     */
    @GetMapping("/latest")
    @Operation(summary = "获取最新版本", description = "获取指定平台的最新版本")
    public Mono<ResponseResult<SysAppVersion>> getLatestVersion(
            @Parameter(description = "平台类型") @RequestParam(defaultValue = "android") String platform) {
        return Mono.fromCallable(() -> sysAppVersionService.getLatestVersion(platform))
                .map(latestVersion -> latestVersion != null
                        ? ResponseResult.success(latestVersion)
                        : ResponseResult.<SysAppVersion>error("未找到版本信息"));
    }

    // ==================== 客户端API (/app/app-version) ====================

    /**
     * 检查更新（客户端调用）
     */
    @GetMapping(value = "/app/app-version/check")
    @Operation(summary = "检查更新", description = "客户端检查是否有新版本可用")
    public Mono<ResponseResult<VersionCheckResponse>> checkUpdate(
            @Parameter(description = "当前版本号") @RequestParam Integer versionCode,
            @Parameter(description = "平台类型") @RequestParam(defaultValue = "android") String platform) {
        return Mono.fromCallable(() -> sysAppVersionService.checkUpdate(versionCode, platform))
                .map(latestVersion -> {
                    if (latestVersion != null) {
                        // 比较版本号判断是否有更新
                        boolean hasUpdate = versionCode < latestVersion.getVersionCode();
                        VersionCheckResponse response = VersionCheckResponse.from(latestVersion, hasUpdate);
                        return ResponseResult.success(response);
                    }
                    return ResponseResult.<VersionCheckResponse>error("未找到版本信息");
                })
                .onErrorResume(e -> {
                    log.error("Check update error: {}", e.getMessage(), e);
                    return Mono.just(ResponseResult.error("检查更新失败: " + e.getMessage()));
                });
    }

    /**
     * 获取最新版本信息（客户端调用）
     */
    @GetMapping(value = "/app/app-version/latest")
    @Operation(summary = "获取最新版本", description = "获取最新版本信息（客户端）")
    public Mono<ResponseResult<VersionCheckResponse>> getLatestVersionForClient(
            @Parameter(description = "平台类型") @RequestParam(defaultValue = "android") String platform) {
        return Mono.fromCallable(() -> sysAppVersionService.getLatestVersion(platform))
                .map(latestVersion -> {
                    if (latestVersion != null) {
                        VersionCheckResponse response = VersionCheckResponse.from(latestVersion, false);
                        return ResponseResult.success(response);
                    }
                    return ResponseResult.<VersionCheckResponse>error("未找到版本信息");
                });
    }

    /**
     * 生成MinIO临时预签名URL（7天有效期）
     *
     * @param objectName 对象名称（文件名）
     * @return 临时访问URL
     */
    private String generatePresignedUrl(String objectName) {
        try {
            return minioUtil.getFileUrl(bucketName, objectName);
        } catch (Exception e) {
            log.error("Generate presigned URL error: {}", e.getMessage());
            // 如果生成失败，返回对象名称作为备用
            return objectName;
        }
    }
}
