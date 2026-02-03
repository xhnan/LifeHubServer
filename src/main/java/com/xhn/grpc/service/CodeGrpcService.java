package com.xhn.grpc.service;

import com.xhn.grpc.client.CodeGenerationClient;

import lifehub.Codegen;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Code gRPC 业务服务
 *
 * @author xhn
 * @date 2026-02-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CodeGrpcService {

    private final CodeGenerationClient codeGenerationClient;

    /**
     * 调用 Python 服务生成代码
     *
     * @param prompt 自然语言描述
     * @return 代码生成响应
     */
    public Mono<Codegen.GenerateResponse> generateCode(String prompt) {
        return Mono.fromCallable(() -> {
            Codegen.GenerateResponse response = codeGenerationClient.generateCode(prompt);
            log.info("Python 服务代码生成完成: success={}, message={}",
                    response.getSuccess(),
                    response.getMessage());
            return response;
        });
    }
}
