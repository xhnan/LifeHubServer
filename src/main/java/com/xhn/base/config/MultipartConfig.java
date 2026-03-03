package com.xhn.base.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * WebFlux Multipart 文件上传配置
 * <p>
 * Spring WebFlux 不使用 spring.servlet.multipart 配置，
 * 需要通过 ServerCodecConfigurer 配置文件上传参数
 *
 * @author xhn
 * @date 2026-03-03
 */
@Configuration
public class MultipartConfig implements WebFluxConfigurer {

    /**
     * 配置 HTTP 消息编解码器
     * 支持 multipart/form-data 和各种文件类型（包括 APK）
     */
    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        // 设置最大内存缓冲区大小（100MB）- 支持大文件上传
        configurer.defaultCodecs().maxInMemorySize(100 * 1024 * 1024);

        // 启用请求详情日志记录（用于调试）
        configurer.defaultCodecs().enableLoggingRequestDetails(true);
    }
}
