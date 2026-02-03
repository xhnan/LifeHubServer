package com.xhn.grpc.controller;

import com.xhn.grpc.service.HealthGrpcService;
import com.xhn.response.ResponseResult;
import lifehub.HealthOuterClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * gRPC 测试控制器
 *
 * @author xhn
 * @date 2026-02-03
 */
@Slf4j
@RestController
@RequestMapping("/grpc/health")
@RequiredArgsConstructor
public class HealthTestController {

    private final HealthGrpcService healthGrpcService;

    /**
     * 测试 gRPC 健康检查接口
     *
     * @return 健康检查结果
     */
    @GetMapping("/check")
    public Mono<ResponseResult<HealthCheckDTO>> check() {
        return healthGrpcService.checkHealth()
                .map(response -> {
                    HealthCheckDTO dto = HealthCheckDTO.builder()
                            .status(response.getStatus())
                            .version(response.getVersion())
                            .timestamp(response.getTimestamp())
                            .build();
                    return ResponseResult.success("gRPC 调用成功", dto);
                })
                .onErrorResume(e -> {
                    log.error("gRPC 调用失败", e);
                    return Mono.just(ResponseResult.error("gRPC 调用失败: " + e.getMessage()));
                });
    }

    /**
     * 测试 gRPC Ping 接口
     *
     * @return Ping 响应
     */
    @GetMapping("/ping")
    public Mono<ResponseResult<PingDTO>> ping() {
        return healthGrpcService.ping()
                .map(response -> {
                    PingDTO dto = PingDTO.builder()
                            .message(response.getMessage())
                            .build();
                    return ResponseResult.success("Ping 成功", dto);
                })
                .onErrorResume(e -> {
                    log.error("gRPC Ping 失败", e);
                    return Mono.just(ResponseResult.error("Ping 失败: " + e.getMessage()));
                });
    }

    /**
     * 健康检查 DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class HealthCheckDTO {
        private String status;
        private String version;
        private Long timestamp;
    }

    /**
     * Ping 响应 DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class PingDTO {
        private String message;
    }
}
