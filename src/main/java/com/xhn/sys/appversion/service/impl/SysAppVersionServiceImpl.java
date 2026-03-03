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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * 应用版本管理 服务实现类
 *
 * @author xhn
 * @date 2026-03-02
 */
@Slf4j
@Service
public class SysAppVersionServiceImpl extends ServiceImpl<SysAppVersionMapper, SysAppVersion>
        implements SysAppVersionService {

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
        String finalPlatform = platform != null ? platform : "android";

        // 检查版本号是否已存在
        LambdaQueryWrapper<SysAppVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysAppVersion::getVersionCode, versionCode)
                .eq(SysAppVersion::getPlatform, finalPlatform);
        SysAppVersion existingVersion = this.getOne(queryWrapper);
        if (existingVersion != null) {
            return Mono.error(new RuntimeException("版本号 " + versionCode + " 已存在"));
        }

        // 生成文件名：app-v{versionCode}-{timestamp}.apk
        String fileName = String.format("app-v%d-%d.apk",
                versionCode, System.currentTimeMillis());

        // 读取 FilePart 内容并上传到 MinIO
        return DataBufferUtils.join(file.content())
                .flatMap(dataBuffer -> {
                    try {
                        byte[] fileBytes = dataBufferAsByteArray(dataBuffer);

                        // 上传文件到MinIO（这里需要适配FilePart）
                        // 注意：minioUtil.uploadFile 需要支持 byte[] 或我们需要创建临时的 MultipartFile
                        // 为了简化，这里创建一个临时的 InputStream
                        java.io.InputStream inputStream = new java.io.ByteArrayInputStream(fileBytes);

                        // 上传到MinIO
                        minioUtil.uploadFile(bucketName, fileName, inputStream,
                                fileBytes.length, file.headers().getContentType().toString());

                        // 计算文件MD5
                        String fileMd5 = calculateMD5(fileBytes);

                        // 创建版本记录
                        SysAppVersion appVersion = new SysAppVersion();
                        appVersion.setVersionCode(versionCode);
                        appVersion.setVersionName(versionName);
                        appVersion.setFileUrl(fileName); // 保存文件名（对象名称）
                        appVersion.setFileSize((long) fileBytes.length);
                        appVersion.setFileMd5(fileMd5);
                        appVersion.setUpdateLog(updateLog);
                        appVersion.setIsForce(isForce != null ? isForce : 0);
                        appVersion.setPlatform(finalPlatform);
                        appVersion.setStatus(1);
                        appVersion.setCreatedAt(LocalDateTime.now());
                        appVersion.setUpdatedAt(LocalDateTime.now());

                        // 保存到数据库
                        this.save(appVersion);

                        log.info("Published new version: {} - {}", versionCode, versionName);
                        return Mono.just(appVersion);

                    } catch (Exception e) {
                        log.error("Publish version error: {}", e.getMessage(), e);
                        return Mono.error(new RuntimeException("发布版本失败: " + e.getMessage(), e));
                    }
                });
    }

    /**
     * 将 DataBuffer 转换为 byte 数组
     */
    private byte[] dataBufferAsByteArray(DataBuffer dataBuffer) {
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        return bytes;
    }

    /**
     * 上传文件到MinIO（支持InputStream）
     *
     * @param bucketName 桶名称
     * @param fileName 文件名
     * @param inputStream 文件流
     * @param size 文件大小
     * @param contentType 内容类型
     */
    private void uploadStreamToMinio(String bucketName, String fileName,
                                     java.io.InputStream inputStream,
                                     long size, String contentType) {
        try {
            minioUtil.uploadFile(bucketName, fileName, inputStream, size, contentType);
        } catch (Exception e) {
            throw new RuntimeException("上传文件到MinIO失败", e);
        }
    }

    @Override
    public SysAppVersion checkUpdate(Integer currentVersionCode, String platform) {
        // 获取最新版本
        SysAppVersion latestVersion = getLatestVersion(platform);

        if (latestVersion == null) {
            return null;
        }

        // 无论是否有更新，都返回最新版本信息
        // 由上层通过比较版本号判断是否有更新
        return latestVersion;
    }

    @Override
    public SysAppVersion getLatestVersion(String platform) {
        LambdaQueryWrapper<SysAppVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysAppVersion::getPlatform, platform != null ? platform : "android")
                .eq(SysAppVersion::getStatus, 1)
                .orderByDesc(SysAppVersion::getVersionCode)
                .last("LIMIT 1");

        SysAppVersion version = this.getOne(queryWrapper);
        if (version != null) {
            // 动态生成临时下载URL
            version.setFileUrl(generatePresignedUrl(version.getFileUrl()));
        }
        return version;
    }

    /**
     * 生成MinIO临时预签名URL
     *
     * @param objectName 对象名称（文件名）
     * @return 临时访问URL（7天有效期）
     */
    private String generatePresignedUrl(String objectName) {
        try {
            return minioUtil.getFileUrl(bucketName, objectName);
        } catch (Exception e) {
            log.error("Generate presigned URL error: {}", e.getMessage());
            // 如果生成失败，返回完整路径URL作为备用
            return String.format("%s/%s/%s",
                    minioEndpoint.replaceAll("/$", ""), bucketName, objectName);
        }
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
                                            Integer isForce, String platform) {
        String finalPlatform = platform != null ? platform : "android";

        // 检查版本号是否已存在
        LambdaQueryWrapper<SysAppVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysAppVersion::getVersionCode, versionCode)
                .eq(SysAppVersion::getPlatform, finalPlatform);
        SysAppVersion existingVersion = this.getOne(queryWrapper);
        if (existingVersion != null) {
            return Mono.error(new RuntimeException("版本号 " + versionCode + " 已存在"));
        }

        // 禁用该平台的所有旧版本
        LambdaQueryWrapper<SysAppVersion> disableWrapper = new LambdaQueryWrapper<>();
        disableWrapper.eq(SysAppVersion::getPlatform, finalPlatform)
                .eq(SysAppVersion::getStatus, 1);
        SysAppVersion updateDisabled = new SysAppVersion();
        updateDisabled.setStatus(0);
        updateDisabled.setUpdatedAt(LocalDateTime.now());
        this.update(updateDisabled, disableWrapper);

        log.info("Disabled all old versions for platform: {}", finalPlatform);

        // 生成文件名：app-v{versionCode}-{timestamp}.apk
        String fileName = String.format("app-v%d-%d.apk",
                versionCode, System.currentTimeMillis());

        // 读取 FilePart 内容并上传到 MinIO
        return DataBufferUtils.join(file.content())
                .flatMap(dataBuffer -> {
                    try {
                        byte[] fileBytes = dataBufferAsByteArray(dataBuffer);

                        // 上传文件到MinIO
                        java.io.InputStream inputStream = new java.io.ByteArrayInputStream(fileBytes);
                        minioUtil.uploadFile(bucketName, fileName, inputStream,
                                fileBytes.length, file.headers().getContentType().toString());

                        // 计算文件MD5
                        String fileMd5 = calculateMD5(fileBytes);

                        // 创建版本记录
                        SysAppVersion appVersion = new SysAppVersion();
                        appVersion.setVersionCode(versionCode);
                        appVersion.setVersionName(versionName);
                        appVersion.setFileUrl(fileName); // 保存文件名（对象名称）
                        appVersion.setFileSize((long) fileBytes.length);
                        appVersion.setFileMd5(fileMd5);
                        appVersion.setUpdateLog(updateLog);
                        appVersion.setIsForce(isForce != null ? isForce : 0);
                        appVersion.setPlatform(finalPlatform);
                        appVersion.setStatus(1); // 新版本自动启用
                        appVersion.setCreatedAt(LocalDateTime.now());
                        appVersion.setUpdatedAt(LocalDateTime.now());

                        // 保存到数据库
                        this.save(appVersion);

                        log.info("Quick published new version: {} - {} (old versions disabled)", versionCode, versionName);
                        return Mono.just(appVersion);

                    } catch (Exception e) {
                        log.error("Quick publish version error: {}", e.getMessage(), e);
                        return Mono.error(new RuntimeException("快速发布版本失败: " + e.getMessage(), e));
                    }
                });
    }

    /**
     * 计算文件的MD5值
     *
     * @param data 文件字节数组
     * @return MD5字符串
     */
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
