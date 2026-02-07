package com.xhn.fin.bookmembers.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 账本成员表实体基类
 * 对应数据库表: fin_book_members
 * @author xhn
 * @date 2026-02-07
 */
@Data
@TableName("fin_book_members")
@Schema(description = "账本成员表")
public class BaseFinBookMembers {

    @Schema(description = "主键ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "账本ID")
    @TableField("book_id")
    private Long bookId;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "角色权限 (OWNER/ADMIN/EDITOR/VIEWER)")
    @TableField("role")
    private String role;

    @Schema(description = "账本内专属昵称")
    @TableField("nickname")
    private String nickname;

    @Schema(description = "加入时间")
    @TableField("joined_at")
    private LocalDateTime joinedAt;
}