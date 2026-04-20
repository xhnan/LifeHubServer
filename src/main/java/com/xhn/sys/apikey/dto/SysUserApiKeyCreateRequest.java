package com.xhn.sys.apikey.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "创建 API Key 请求")
public class SysUserApiKeyCreateRequest {

    @Schema(description = "Key 名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String keyName;

    @Schema(description = "用途说明")
    private String description;

    @Schema(description = "允许访问的接口路径范围，支持精确路径和 /** 前缀匹配")
    private List<String> allowedPaths;

    @Schema(description = "过期时间，可选")
    private LocalDateTime expiresAt;
}
