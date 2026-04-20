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
@Schema(description = "用户 API Key 列表项")
public class SysUserApiKeyView {

    @Schema(description = "API Key ID")
    private Long id;

    @Schema(description = "Key 名称")
    private String keyName;

    @Schema(description = "脱敏后的 Key")
    private String maskedKey;

    @Schema(description = "用途说明")
    private String description;

    @Schema(description = "允许访问的接口路径范围")
    private List<String> allowedPaths;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "最后使用时间")
    private LocalDateTime lastUsedAt;

    @Schema(description = "过期时间")
    private LocalDateTime expiresAt;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
