package com.xhn.ai.controller;

import com.xhn.ai.model.CodeGenerateRequest;
import com.xhn.ai.model.CodeGenerateResponse;
import com.xhn.ai.model.FileInfo;
import com.xhn.grpc.service.CodeGrpcService;

import com.xhn.response.ResponseResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 代码生成控制器
 *
 * @author xhn
 * @date 2026/2/3 15:50
 */
@Slf4j
@RestController
@RequestMapping("/ai/code")
@RequiredArgsConstructor
public class CodeController {

    private final CodeGrpcService codeGrpcService;

    /**
     * 代码生成接口
     *
     * @param request 代码生成请求
     * @return 代码生成结果
     */
    @PostMapping("/generate")
    public Mono<ResponseResult<CodeGenerateResponse>> generate(@RequestBody @Valid CodeGenerateRequest request) {
        log.info("收到代码生成请求，prompt: {}", request.getPrompt());

        return codeGrpcService.generateCode(request.getPrompt())
                .map(response -> {
                    CodeGenerateResponse dto = new CodeGenerateResponse();
                    dto.setSuccess(response.getSuccess());
                    dto.setMessage(response.getMessage());
                    dto.setDescription(response.getDescription());
                    dto.setError(response.getError());

                    // 转换文件列表
                    if (response.getFilesCount() > 0) {
                        List<FileInfo> files = response.getFilesList().stream()
                                .map(file -> {
                                    FileInfo info = new FileInfo();
                                    info.setPath(file.getPath());
                                    info.setType(file.getType());
                                    info.setDescription(file.getDescription());
                                    return info;
                                })
                                .collect(Collectors.toList());
                        dto.setFiles(files);
                    }

                    // 转换步骤
                    if (response.getStepsCount() > 0) {
                        dto.setSteps(response.getStepsList());
                    }

                    if (response.getSuccess()) {
                        return ResponseResult.<CodeGenerateResponse>success("代码生成成功", dto);
                    } else {
                        return ResponseResult.<CodeGenerateResponse>error("代码生成失败: " + response.getError());
                    }
                })
                .onErrorResume(e -> {
                    log.error("代码生成失败", e);
                    return Mono.just(ResponseResult.<CodeGenerateResponse>error("代码生成失败: " + e.getMessage()));
                });
    }

    /**
     * 健康检查接口
     *
     * @return 健康状态
     */
    @GetMapping("/health")
    public Mono<ResponseResult<String>> health() {
        return Mono.just(ResponseResult.success("AI Code Controller is ready"));
    }
}
