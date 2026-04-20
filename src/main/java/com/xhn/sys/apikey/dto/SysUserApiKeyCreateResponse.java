package com.xhn.sys.apikey.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "创建 API Key 响应")
public class SysUserApiKeyCreateResponse {

    @Schema(description = "API Key ID")
    private Long id;

    @Schema(description = "明文 API Key，仅创建时返回一次")
    private String apiKey;

    @Schema(description = "Key 名称")
    private String keyName;

    @Schema(description = "前缀")
    private String keyPrefix;

    @Schema(description = "允许访问的接口路径范围")
    private List<String> allowedPaths;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
