package com.xhn.grpc.service;

import lifehub.HealthOuterClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.xhn.grpc.client.PythonHealthClient;

/**
 * Health gRPC 业务服务
 *
 * @author xhn
 * @date 2026-02-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HealthGrpcService {

    private final PythonHealthClient pythonHealthClient;

    /**
     * 检查 Python 服务健康状态
     *
     * @return 健康状态信息
     */
    public Mono<HealthOuterClass.HealthResponse> checkHealth() {
        return Mono.fromCallable(() -> {
            HealthOuterClass.HealthResponse response = pythonHealthClient.check();
            log.info("Python 服务健康检查完成: {}", response.getStatus());
            return response;
        });
    }

    /**
     * 调用 Python 服务 Ping 接口
     *
     * @return Ping 响应
     */
    public Mono<HealthOuterClass.PingResponse> ping() {
        return Mono.fromCallable(() -> {
            HealthOuterClass.PingResponse response = pythonHealthClient.ping();
            log.info("Python 服务 Ping 完成: {}", response.getMessage());
            return response;
        });
    }
}
