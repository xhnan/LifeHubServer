package com.xhn.ai.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 代码生成请求
 *
 * @author xhn
 * @date 2026-02-03
 */
@Data
public class CodeGenerateRequest {

    /**
     * 自然语言描述
     */
    @NotBlank(message = "提示词不能为空")
    private String prompt;
}
