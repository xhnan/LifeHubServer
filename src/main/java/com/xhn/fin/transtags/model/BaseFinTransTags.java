package com.xhn.fin.transtags.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 交易标签关联表实体基类
 * 
 * @author xhn
 * @date 2026-02-07
 */
@Data
@TableName("fin_trans_tags")
@Schema(description = "交易标签关联表")
public class BaseFinTransTags {

    @Schema(description = "主键ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "交易ID")
    @TableField("trans_id")
    private Long transId;

    @Schema(description = "标签ID")
    @TableField("tag_id")
    private Long tagId;
}