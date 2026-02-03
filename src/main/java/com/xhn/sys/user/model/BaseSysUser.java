package com.xhn.sys.user.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户表基类
 * @author xhn
 * @date 2026-02-03
 */
@Data
@TableName("sys_user")
@Schema(description = "用户表")
public class BaseSysUser {

    @Schema(description = "用户ID，主键，自增")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    @Schema(description = "用户名，唯一")
    @TableField("username")
    private String username;

    @Schema(description = "用户邮箱，唯一")
    @TableField("email")
    private String email;

    @Schema(description = "用户密码（存储密码的哈希值）")
    @TableField("password")
    private String password;

    @Schema(description = "用户全名")
    @TableField("full_name")
    private String fullName;

    @Schema(description = "用户头像的URL")
    @TableField("avatar_url")
    private String avatarUrl;

    @Schema(description = "用户性别（例如：male, female, other）")
    @TableField("gender")
    private String gender;

    @Schema(description = "用户的生日")
    @TableField("birth_date")
    private LocalDate birthDate;

    @Schema(description = "用户账户状态（例如：active, inactive, banned）")
    @TableField("status")
    private String status;

    @Schema(description = "用户最后一次登录时间")
    @TableField("last_login")
    private LocalDateTime lastLogin;

    @Schema(description = "用户登录失败次数")
    @TableField("login_attempts")
    private Integer loginAttempts;

    @Schema(description = "用户注册时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @Schema(description = "最后更新时间")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @Schema(description = "用户软删除时间")
    @TableField("deleted_at")
    private LocalDateTime deletedAt;

    @Schema(description = "邮箱是否验证")
    @TableField("email_verified")
    private Boolean emailVerified;

    @Schema(description = "手机是否验证")
    @TableField("phone_verified")
    private Boolean phoneVerified;

    @Schema(description = "是否启用两步验证")
    @TableField("two_factor_enabled")
    private Boolean twoFactorEnabled;

    @Schema(description = "软删除标志")
    @TableField("is_deleted")
    @TableLogic
    private Boolean isDeleted;
}