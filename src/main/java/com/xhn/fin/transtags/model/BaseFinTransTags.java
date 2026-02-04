package com.xhn.fin.transtags.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 财务交易标签关联表基类
 * 
 * @author xhn
 * @date 2026-02-04
 */
@Data
@TableName("fin_trans_tags")
@Schema(description = "财务交易标签关联表")
public class BaseFinTransTags {
    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "交易ID")
    private Long transId;

    @Schema(description = "标签ID")
    private Long tagId;
}