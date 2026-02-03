package com.xhn.grpc.client;

import lifehub.HealthGrpc;
import lifehub.HealthOuterClass;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

/**
 * Python Health gRPC 客户端
 *
 * @author xhn
 * @date 2026-02-03
 */
@Slf4j
@Component
public class PythonHealthClient {

    /**
     * gRPC 客户端注入
     * beanName 对应配置文件中的 grpc.client.python-health
     */
    @GrpcClient("python-health")
    private HealthGrpc.HealthBlockingStub healthStub;

    /**
     * 调用 Python 服务的健康检查接口
     *
     * @return 健康检查响应
     */
    public HealthOuterClass.HealthResponse check() {
        log.info("调用 gRPC Health.Check 接口");

        HealthOuterClass.Empty request = HealthOuterClass.Empty.newBuilder().build();
        HealthOuterClass.HealthResponse response = healthStub.check(request);

        log.info("gRPC 响应: status={}, version={}, timestamp={}",
                response.getStatus(),
                response.getVersion(),
                response.getTimestamp());

        return response;
    }

    /**
     * 调用 Python 服务的 Ping 接口（测试用）
     *
     * @return Ping 响应
     */
    public HealthOuterClass.PingResponse ping() {
        log.info("调用 gRPC Health.Ping 接口");

        HealthOuterClass.Empty request = HealthOuterClass.Empty.newBuilder().build();
        HealthOuterClass.PingResponse response = healthStub.ping(request);

        log.info("gRPC Ping 响应: message={}", response.getMessage());

        return response;
    }
}
