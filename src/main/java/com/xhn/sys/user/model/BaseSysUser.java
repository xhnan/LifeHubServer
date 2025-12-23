package com.xhn.sys.user.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * @author xhn
 * @date 2025-12-23 10:24:10
 */
@Data
@TableName("sys_user")
public class BaseSysUser {

    /**
     * 用户ID，主键，自增
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    /**
     * 用户名，唯一
     */
    @TableField("username")
    private String username;

    /**
     * 用户邮箱，唯一
     */
    @TableField("email")
    private String email;

    /**
     * 用户密码（存储密码的哈希值）
     */
    @TableField("password")
    private String password;

    /**
     * 用户全名
     */
    @TableField("full_name")
    private String fullName;

    /**
     * 用户头像的URL
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 用户性别（例如：male, female, other）
     */
    @TableField("gender")
    private String gender;

    /**
     * 用户的生日
     */
    @TableField("birth_date")
    private LocalDate birthDate;

    /**
     * 用户账户状态（例如：active, inactive, banned）
     */
    @TableField("status")
    private String status;

    /**
     * 用户最后一次登录时间
     */
    @TableField("last_login")
    private LocalDateTime lastLogin;

    /**
     * 用户登录失败次数
     */
    @TableField("login_attempts")
    private Integer loginAttempts;

    /**
     * 用户注册时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 最后更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 用户软删除时间
     */
    @TableField("deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 邮箱是否验证
     */
    @TableField("email_verified")
    private Boolean emailVerified;

    /**
     * 手机是否验证
     */
    @TableField("phone_verified")
    private Boolean phoneVerified;

    /**
     * 是否启用两步验证
     */
    @TableField("two_factor_enabled")
    private Boolean twoFactorEnabled;

    /**
     * 软删除标志
     */
    @TableField("is_deleted")
    private Boolean isDeleted;

}