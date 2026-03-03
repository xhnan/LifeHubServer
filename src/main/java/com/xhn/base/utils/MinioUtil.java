package com.xhn.base.utils;

import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * MinIO 工具类
 *
 * @author xhn
 * @date 2026-03-02
 */
@Slf4j
@Component
public class MinioUtil {

    @Autowired
    private MinioClient minioClient;

    /**
     * 检查存储桶是否存在，不存在则创建
     *
     * @param bucketName 存储桶名称
     */
    public void bucketExists(String bucketName) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                log.info("Created bucket: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("Check bucket exists error: {}", e.getMessage());
            throw new RuntimeException("检查存储桶失败", e);
        }
    }

    /**
     * 上传文件
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称（文件名）
     * @param file       文件
     * @return 文件访问路径
     */
    public String uploadFile(String bucketName, String objectName, MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            // 检查存储桶是否存在
            bucketExists(bucketName);

            // 上传文件
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            log.info("File uploaded successfully: {}/{}", bucketName, objectName);
            return objectName;
        } catch (Exception e) {
            log.error("File upload error: {}", e.getMessage());
            throw new RuntimeException("文件上传失败", e);
        }
    }

    /**
     * 上传文件流
     *
     * @param bucketName  存储桶名称
     * @param objectName  对象名称（文件名）
     * @param inputStream 文件流
     * @param size        文件大小
     * @param contentType 文件类型
     * @return 文件访问路径
     */
    public String uploadFile(String bucketName, String objectName, InputStream inputStream,
                             long size, String contentType) {
        try {
            // 检查存储桶是否存在
            bucketExists(bucketName);

            // 上传文件
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, size, -1)
                    .contentType(contentType)
                    .build());

            log.info("File uploaded successfully: {}/{}", bucketName, objectName);
            return objectName;
        } catch (Exception e) {
            log.error("File upload error: {}", e.getMessage());
            throw new RuntimeException("文件上传失败", e);
        }
    }

    /**
     * 获取文件访问URL（临时URL，7天有效期）
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 文件访问URL
     */
    public String getFileUrl(String bucketName, String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectName)
                    .expiry(7, TimeUnit.DAYS)
                    .build());
        } catch (Exception e) {
            log.error("Get file URL error: {}", e.getMessage());
            throw new RuntimeException("获取文件URL失败", e);
        }
    }

    /**
     * 获取永久访问URL（需要存储桶设置为公共访问）
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 文件访问URL
     */
    public String getPublicFileUrl(String bucketName, String objectName) {
        // 假设MinIO服务端地址格式为 http://host:port
        String endpoint = minioClient.toString();
        // 提取endpoint（MinioClient的toString返回地址信息）
        return String.format("%s/%s/%s", endpoint, bucketName, objectName);
    }

    /**
     * 下载文件
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 文件流
     */
    public InputStream downloadFile(String bucketName, String objectName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.error("File download error: {}", e.getMessage());
            throw new RuntimeException("文件下载失败", e);
        }
    }

    /**
     * 删除文件
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     */
    public void deleteFile(String bucketName, String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            log.info("File deleted successfully: {}/{}", bucketName, objectName);
        } catch (Exception e) {
            log.error("File delete error: {}", e.getMessage());
            throw new RuntimeException("文件删除失败", e);
        }
    }

    /**
     * 获取文件信息
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @return 文件信息
     */
    public StatObjectResponse getFileInfo(String bucketName, String objectName) {
        try {
            return minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.error("Get file info error: {}", e.getMessage());
            throw new RuntimeException("获取文件信息失败", e);
        }
    }
}
