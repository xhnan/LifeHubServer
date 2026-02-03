package com.xhn.grpc.client;

import lifehub.Codegen;
import lifehub.CodeGenerationGrpc;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

/**
 * Python 代码生成 gRPC 客户端
 *
 * @author xhn
 * @date 2026-02-03
 */
@Slf4j
@Component
public class CodeGenerationClient {

    /**
     * gRPC 客户端注入
     * beanName 对应配置文件中的 grpc.client.python-codegen
     */
    @GrpcClient("python-codegen")
    private CodeGenerationGrpc.CodeGenerationBlockingStub codegenStub;

    /**
     * 调用 Python 服务的代码生成接口
     *
     * @param prompt 自然语言描述
     * @return 代码生成响应
     */
    public Codegen.GenerateResponse generateCode(String prompt) {
        log.info("调用 gRPC CodeGeneration.GenerateCode 接口，prompt: {}", prompt);

        Codegen.GenerateRequest request = Codegen.GenerateRequest.newBuilder()
                .setPrompt(prompt)
                .build();

        Codegen.GenerateResponse response = codegenStub.generateCode(request);

        log.info("gRPC 代码生成响应: success={}, message={}, files count={}",
                response.getSuccess(),
                response.getMessage(),
                response.getFilesCount());

        return response;
    }
}
