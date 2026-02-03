package com.xhn.ai.model;

import lombok.Data;

import java.util.List;

/**
 * 代码生成响应
 *
 * @author xhn
 * @date 2026-02-03
 */
@Data
public class CodeGenerateResponse {

    private Boolean success;
    private String message;
    private String description;
    private String error;
    private List<FileInfo> files;
    private List<String> steps;
}
