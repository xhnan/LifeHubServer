package com.xhn.base.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 对象存储配置类
 *
 * @author xhn
 * @date 2026-03-02
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    /**
     * MinIO服务端地址
     */
    private String url;

    /**
     * MinIO访问密钥
     */
    private String accessKey;

    /**
     * MinIO密钥
     */
    private String secretKey;

    /**
     * MinIO存储桶名称
     */
    private String bucketName;

    /**
     * 创建MinIO客户端
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }
}
