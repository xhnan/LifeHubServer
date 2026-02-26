package com.xhn.wechat.message.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 企业微信消息记录表实体基类
 * 表名: wechat_message
 * @author xhn
 * @date 2026-02-26
 */
@Data
@TableName("wechat_message")
@Schema(description = "企业微信消息记录表实体基类")
public class BaseWeChatMessage {

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "应用ID(关联wechat_app_config.id)")
    @TableField("app_id")
    private Long appId;

    @Schema(description = "消息方向(inbound=接收,outbound=发送)")
    @TableField("msg_direction")
    private String msgDirection;

    @Schema(description = "消息类型(text,image,voice,video,file,event等)")
    @TableField("msg_type")
    private String msgType;

    @Schema(description = "发送方UserID")
    @TableField("from_user")
    private String fromUser;

    @Schema(description = "接收方UserID")
    @TableField("to_user")
    private String toUser;

    @Schema(description = "消息内容")
    @TableField("content")
    private String content;

    @Schema(description = "消息ID")
    @TableField("msg_id")
    private String msgId;

    @Schema(description = "消息状态(success,failed,pending)")
    @TableField("status")
    private String status;

    @Schema(description = "错误码")
    @TableField("error_code")
    private String errorCode;

    @Schema(description = "错误信息")
    @TableField("error_msg")
    private String errorMsg;

    @Schema(description = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
