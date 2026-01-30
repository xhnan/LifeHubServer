package com.xhn.test.controller;

import com.xhn.external.grpc.EchoRequest;
import com.xhn.external.grpc.EchoResponse;
import com.xhn.external.grpc.TestServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试调用 Python gRPC 服务
 */
@Slf4j
@RestController
public class TestGrpcController {

    /**
     * 注入 Python gRPC 服务客户端
     * 配置名：python-grpc
     */
    @GrpcClient("python-grpc")
    private TestServiceGrpc.TestServiceBlockingStub testServiceStub;

    /**
     * 测试接口：调用 Python gRPC 服务
     *
     * 访问地址：http://localhost:8080/test/grpc/echo?message=Hello
     *
     * @param message 要发送的消息
     * @return Python 服务返回的消息
     */
    @GetMapping("/test/grpc/echo")
    public String testEcho(@RequestParam(defaultValue = "Hello from Java") String message) {
        log.info("========== 开始测试 Python gRPC 调用 ==========");
        log.info("发送消息: {}", message);

        try {
            // 构建请求
            EchoRequest request = EchoRequest.newBuilder()
                    .setMessage(message)
                    .build();

            log.info("调用 gRPC 接口: TestService.Echo");

            // 调用 Python gRPC 服务
            EchoResponse response = testServiceStub.echo(request);

            log.info("收到响应: {}", response.getMessage());
            log.info("时间戳: {}", response.getTimestamp());
            log.info("========== 测试成功 ==========");

            return "✅ 成功！\n" +
                   "发送: " + message + "\n" +
                   "收到: " + response.getMessage() + "\n" +
                   "时间戳: " + response.getTimestamp();

        } catch (Exception e) {
            log.error("========== 调用失败 ==========", e);
            return "❌ 失败: " + e.getMessage();
        }
    }
}
