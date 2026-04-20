package com.xhn.sys.appversion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xhn.base.utils.MinioUtil;
import com.xhn.sys.appversion.mapper.SysAppVersionMapper;
import com.xhn.sys.appversion.model.SysAppVersion;
import com.xhn.sys.appversion.service.SysAppVersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Service
public class SysAppVersionServiceImpl extends ServiceImpl<SysAppVersionMapper, SysAppVersion>
        implements SysAppVersionService {

    private static final String DEFAULT_PLATFORM = "android";
    private static final String UNKNOWN_SOURCE = "UNKNOWN";

    @Autowired
    private MinioUtil minioUtil;

    @Value("${minio.bucket-name:lifehub}")
    private String bucketName;

    @Value("${minio.url:http://localhost:9000}")
    private String minioEndpoint;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Mono<SysAppVersion> publishVersion(Integer versionCode, String versionName,
                                              FilePart file, String updateLog,
                                              Integer isForce, String platform) {
        String finalPlatform = normalizePlatform(platform);
        validateVersionNotExists(versionCode, finalPlatform);

        String fileName = buildFileName(versionCode);
        return DataBufferUtils.join(file.content())
                .flatMap(dataBuffer -> savePublishedVersion(versionCode, versionName, file, updateLog,
                        isForce, finalPlatform, fileName, dataBuffer,
                        null, null, "JWT"));
    }

    @Override
    public SysAppVersion checkUpdate(Integer currentVersionCode, String platform) {
        return getLatestVersion(platform);
    }

    @Override
    public SysAppVersion getLatestVersion(String platform) {
        LambdaQueryWrapper<SysAppVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysAppVersion::getPlatform, normalizePlatform(platform))
                .eq(SysAppVersion::getStatus, 1)
                .orderByDesc(SysAppVersion::getVersionCode)
                .last("LIMIT 1");

        SysAppVersion version = this.getOne(queryWrapper);
        if (version != null) {
            version.setFileUrl(generatePresignedUrl(version.getFileUrl()));
        }
        return version;
    }

    @Override
    public IPage<SysAppVersion> pageList(Page<SysAppVersion> page, String platform) {
        LambdaQueryWrapper<SysAppVersion> queryWrapper = new LambdaQueryWrapper<>();
        if (platform != null && !platform.isEmpty()) {
            queryWrapper.eq(SysAppVersion::getPlatform, platform);
        }
        queryWrapper.orderByDesc(SysAppVersion::getVersionCode);
        return this.page(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Mono<SysAppVersion> quickPublish(Integer versionCode, String versionName,
                                            FilePart file, String updateLog,
                                            Integer isForce, String platform,
                                            Long publishedByUserId, Long publishedByApiKeyId,
                                            String publishSource) {
        String finalPlatform = normalizePlatform(platform);
        validateVersionNotExists(versionCode, finalPlatform);

        LambdaQueryWrapper<SysAppVersion> disableWrapper = new LambdaQueryWrapper<>();
        disableWrapper.eq(SysAppVersion::getPlatform, finalPlatform)
                .eq(SysAppVersion::getStatus, 1);
        SysAppVersion updateDisabled = new SysAppVersion();
        updateDisabled.setStatus(0);
        updateDisabled.setUpdatedAt(LocalDateTime.now());
        this.update(updateDisabled, disableWrapper);

        log.info("Disabled old active versions for platform {}", finalPlatform);

        String fileName = buildFileName(versionCode);
        return DataBufferUtils.join(file.content())
                .flatMap(dataBuffer -> savePublishedVersion(versionCode, versionName, file, updateLog,
                        isForce, finalPlatform, fileName, dataBuffer,
                        publishedByUserId, publishedByApiKeyId, normalizePublishSource(publishSource)));
    }

    private Mono<SysAppVersion> savePublishedVersion(Integer versionCode,
                                                     String versionName,
                                                     FilePart file,
                                                     String updateLog,
                                                     Integer isForce,
                                                     String platform,
                                                     String fileName,
                                                     DataBuffer dataBuffer,
                                                     Long publishedByUserId,
                                                     Long publishedByApiKeyId,
                                                     String publishSource) {
        try {
            byte[] fileBytes = dataBufferAsByteArray(dataBuffer);
            java.io.InputStream inputStream = new java.io.ByteArrayInputStream(fileBytes);
            String contentType = file.headers().getContentType() == null
                    ? "application/octet-stream"
                    : file.headers().getContentType().toString();

            minioUtil.uploadFile(bucketName, fileName, inputStream, fileBytes.length, contentType);

            SysAppVersion appVersion = new SysAppVersion();
            appVersion.setVersionCode(versionCode);
            appVersion.setVersionName(versionName);
            appVersion.setFileUrl(fileName);
            appVersion.setFileSize((long) fileBytes.length);
            appVersion.setFileMd5(calculateMD5(fileBytes));
            appVersion.setUpdateLog(updateLog);
            appVersion.setIsForce(isForce != null ? isForce : 0);
            appVersion.setPlatform(platform);
            appVersion.setStatus(1);
            appVersion.setPublishedByUserId(publishedByUserId);
            appVersion.setPublishedByApiKeyId(publishedByApiKeyId);
            appVersion.setPublishSource(publishSource);
            appVersion.setCreatedAt(LocalDateTime.now());
            appVersion.setUpdatedAt(LocalDateTime.now());

            this.save(appVersion);

            log.info("Published version code={}, name={}, source={}, userId={}, apiKeyId={}",
                    versionCode, versionName, publishSource, publishedByUserId, publishedByApiKeyId);
            return Mono.just(appVersion);
        } catch (Exception e) {
            log.error("Publish version failed", e);
            return Mono.error(new RuntimeException("发布版本失败: " + e.getMessage(), e));
        }
    }

    private void validateVersionNotExists(Integer versionCode, String platform) {
        LambdaQueryWrapper<SysAppVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysAppVersion::getVersionCode, versionCode)
                .eq(SysAppVersion::getPlatform, platform);
        SysAppVersion existingVersion = this.getOne(queryWrapper);
        if (existingVersion != null) {
            throw new RuntimeException("版本号 " + versionCode + " 已存在");
        }
    }

    private String normalizePlatform(String platform) {
        return platform != null ? platform : DEFAULT_PLATFORM;
    }

    private String normalizePublishSource(String publishSource) {
        return (publishSource == null || publishSource.isBlank()) ? UNKNOWN_SOURCE : publishSource;
    }

    private String buildFileName(Integer versionCode) {
        return String.format("app-v%d-%d.apk", versionCode, System.currentTimeMillis());
    }

    private byte[] dataBufferAsByteArray(DataBuffer dataBuffer) {
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        return bytes;
    }

    private String generatePresignedUrl(String objectName) {
        try {
            return minioUtil.getFileUrl(bucketName, objectName);
        } catch (Exception e) {
            log.error("Generate presigned URL error: {}", e.getMessage());
            return String.format("%s/%s/%s", minioEndpoint.replaceAll("/$", ""), bucketName, objectName);
        }
    }

    private String calculateMD5(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(data);
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            log.error("Calculate MD5 error: {}", e.getMessage());
            return null;
        }
    }
}
