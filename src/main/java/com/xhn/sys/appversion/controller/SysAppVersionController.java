package com.xhn.sys.appversion.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xhn.base.utils.MinioUtil;
import com.xhn.base.utils.SecurityUtils;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/sys/app-version")
@Tag(name = "应用版本", description = "应用版本管理接口")
public class SysAppVersionController {

    @Autowired
    private SysAppVersionService sysAppVersionService;

    @Autowired
    private MinioUtil minioUtil;

    @Value("${minio.bucket-name:lifehub}")
    private String bucketName;

    @PostMapping(value = "/quick-publish", consumes = "multipart/form-data")
    @Operation(summary = "快速发布版本", description = "适用于第三方或自动化任务代用户快速发布版本")
    public Mono<ResponseResult<SysAppVersion>> quickPublish(
            @Parameter(description = "版本编码") @RequestPart("versionCode") FormFieldPart versionCodePart,
            @Parameter(description = "版本名称") @RequestPart("versionName") FormFieldPart versionNamePart,
            @Parameter(description = "安装包文件") @RequestPart("file") FilePart file,
            @Parameter(description = "更新日志") @RequestPart(value = "updateLog", required = false) FormFieldPart updateLogPart,
            @Parameter(description = "是否强制更新") @RequestPart(value = "isForce", required = false) FormFieldPart isForcePart,
            @Parameter(description = "平台") @RequestPart(value = "platform", required = false) FormFieldPart platformPart) {

        String versionCode = versionCodePart.value();
        String versionName = versionNamePart.value();
        String updateLog = updateLogPart != null ? updateLogPart.value() : null;
        String isForce = isForcePart != null ? isForcePart.value() : "0";
        String platform = platformPart != null ? platformPart.value() : "android";

        Mono<Long> userIdMono = SecurityUtils.getCurrentUserId().defaultIfEmpty(-1L);
        Mono<Long> apiKeyIdMono = SecurityUtils.getCurrentApiKeyId().defaultIfEmpty(-1L);
        Mono<String> authTypeMono = SecurityUtils.getCurrentAuthType().defaultIfEmpty("UNKNOWN");

        return Mono.zip(userIdMono, apiKeyIdMono, authTypeMono)
                .flatMap(tuple -> {
                    Long userId = tuple.getT1() > 0 ? tuple.getT1() : null;
                    Long apiKeyId = tuple.getT2() > 0 ? tuple.getT2() : null;
                    String authType = tuple.getT3();

                    log.info("Quick publish versionCode={}, file={}, authType={}, userId={}, apiKeyId={}",
                            versionCode, file.filename(), authType, userId, apiKeyId);

                    return sysAppVersionService.quickPublish(
                            Integer.valueOf(versionCode), versionName, file, updateLog,
                            Integer.valueOf(isForce), platform, userId, apiKeyId, authType
                    );
                })
                .map(ResponseResult::success)
                .onErrorResume(e -> {
                    log.error("Quick publish error: {}", e.getMessage(), e);
                    return Mono.just(ResponseResult.error("快速发布失败: " + e.getMessage()));
                });
    }

    @PostMapping(value = "/publish", consumes = "multipart/form-data")
    @Operation(summary = "发布版本")
    public Mono<ResponseResult<SysAppVersion>> publishVersion(
            @Parameter(description = "版本编码") @RequestPart("versionCode") FormFieldPart versionCodePart,
            @Parameter(description = "版本名称") @RequestPart("versionName") FormFieldPart versionNamePart,
            @Parameter(description = "安装包文件") @RequestPart("file") FilePart file,
            @Parameter(description = "更新日志") @RequestPart(value = "updateLog", required = false) FormFieldPart updateLogPart,
            @Parameter(description = "是否强制更新") @RequestPart(value = "isForce", required = false) FormFieldPart isForcePart,
            @Parameter(description = "平台") @RequestPart(value = "platform", required = false) FormFieldPart platformPart) {

        String versionCode = versionCodePart.value();
        String versionName = versionNamePart.value();
        String updateLog = updateLogPart != null ? updateLogPart.value() : null;
        String isForce = isForcePart != null ? isForcePart.value() : "0";
        String platform = platformPart != null ? platformPart.value() : "android";

        return sysAppVersionService.publishVersion(
                        Integer.valueOf(versionCode), versionName, file, updateLog,
                        Integer.valueOf(isForce), platform)
                .map(ResponseResult::success)
                .onErrorResume(e -> {
                    log.error("Publish version error: {}", e.getMessage(), e);
                    return Mono.just(ResponseResult.error("发布版本失败: " + e.getMessage()));
                });
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除版本")
    public Mono<ResponseResult<Boolean>> deleteVersion(@PathVariable Long id) {
        return Mono.fromCallable(() -> sysAppVersionService.removeById(id))
                .map(result -> result ? ResponseResult.success(true) : ResponseResult.error("删除失败"));
    }

    @PutMapping("")
    @Operation(summary = "更新版本")
    public Mono<ResponseResult<Boolean>> updateVersion(@RequestBody SysAppVersion appVersion) {
        return Mono.fromCallable(() -> sysAppVersionService.updateById(appVersion))
                .map(result -> result ? ResponseResult.success(true) : ResponseResult.error("更新失败"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "按 ID 查询版本")
    public Mono<ResponseResult<SysAppVersion>> getVersionById(@PathVariable Long id) {
        return Mono.fromCallable(() -> sysAppVersionService.getById(id))
                .map(appVersion -> {
                    if (appVersion != null) {
                        appVersion.setFileUrl(generatePresignedUrl(appVersion.getFileUrl()));
                        return ResponseResult.success(appVersion);
                    }
                    return ResponseResult.<SysAppVersion>error("未找到版本");
                });
    }

    @GetMapping("")
    @Operation(summary = "分页查询版本")
    public Mono<ResponseResult<Page<SysAppVersion>>> listVersions(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "平台") @RequestParam(required = false) String platform) {
        return Mono.fromCallable(() -> {
            Page<SysAppVersion> page = new Page<>(current, size);
            Page<SysAppVersion> result = (Page<SysAppVersion>) sysAppVersionService.pageList(page, platform);
            result.getRecords().forEach(version -> version.setFileUrl(generatePresignedUrl(version.getFileUrl())));
            return result;
        }).map(ResponseResult::success);
    }

    @GetMapping("/latest")
    @Operation(summary = "查询最新版本")
    public Mono<ResponseResult<SysAppVersion>> getLatestVersion(
            @Parameter(description = "平台") @RequestParam(defaultValue = "android") String platform) {
        return Mono.fromCallable(() -> sysAppVersionService.getLatestVersion(platform))
                .map(latestVersion -> latestVersion != null
                        ? ResponseResult.success(latestVersion)
                        : ResponseResult.<SysAppVersion>error("未找到版本"));
    }

    @GetMapping(value = "/app/app-version/check")
    @Operation(summary = "客户端检查更新")
    public Mono<ResponseResult<VersionCheckResponse>> checkUpdate(
            @Parameter(description = "当前版本编码") @RequestParam Integer versionCode,
            @Parameter(description = "平台") @RequestParam(defaultValue = "android") String platform) {
        return Mono.fromCallable(() -> sysAppVersionService.checkUpdate(versionCode, platform))
                .map(latestVersion -> {
                    if (latestVersion != null) {
                        boolean hasUpdate = versionCode < latestVersion.getVersionCode();
                        VersionCheckResponse response = VersionCheckResponse.from(latestVersion, hasUpdate);
                        return ResponseResult.success(response);
                    }
                    return ResponseResult.<VersionCheckResponse>error("未找到版本");
                })
                .onErrorResume(e -> {
                    log.error("Check update error: {}", e.getMessage(), e);
                    return Mono.just(ResponseResult.error("检查更新失败: " + e.getMessage()));
                });
    }

    @GetMapping(value = "/app/app-version/latest")
    @Operation(summary = "客户端获取最新版本")
    public Mono<ResponseResult<VersionCheckResponse>> getLatestVersionForClient(
            @Parameter(description = "平台") @RequestParam(defaultValue = "android") String platform) {
        return Mono.fromCallable(() -> sysAppVersionService.getLatestVersion(platform))
                .map(latestVersion -> {
                    if (latestVersion != null) {
                        VersionCheckResponse response = VersionCheckResponse.from(latestVersion, false);
                        return ResponseResult.success(response);
                    }
                    return ResponseResult.<VersionCheckResponse>error("未找到版本");
                });
    }

    private String generatePresignedUrl(String objectName) {
        try {
            return minioUtil.getFileUrl(bucketName, objectName);
        } catch (Exception e) {
            log.error("Generate presigned URL error: {}", e.getMessage());
            return objectName;
        }
    }
}
