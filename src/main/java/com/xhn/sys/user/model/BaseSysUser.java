package com.xhn.sys.user.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 系统用户实体类
 */
@Data
@TableName("sys_user")
public class BaseSysUser {

    /**
     * 用户ID，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Integer userId;

    /**
     * 用户名，唯一
     */
    private String username;

    /**
     * 用户邮箱，唯一
     */
    private String email;

    /**
     * 用户密码（存储密码的哈希值）
     */
    private String password;

    /**
     * 用户全名
     */
    private String fullName;

    /**
     * 用户头像的URL
     */
    private String avatarUrl;

    /**
     * 用户性别（例如：male, female, other）
     */
    private String gender;

    /**
     * 用户的生日
     */
    private LocalDate birthDate;

    /**
     * 用户账户状态（例如：active, inactive, banned）
     */
    private String status;

    /**
     * 用户最后一次登录时间
     */
    private LocalDateTime lastLogin;

    /**
     * 用户登录失败次数
     */
    private Integer loginAttempts;

    /**
     * 用户注册时间
     */
    private LocalDateTime createdAt;

    /**
     * 最后更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 用户软删除时间
     */
    private LocalDateTime deletedAt;

    /**
     * 邮箱是否验证
     */
    private Boolean emailVerified;

    /**
     * 手机是否验证
     */
    private Boolean phoneVerified;

    /**
     * 是否启用两步验证
     */
    private Boolean twoFactorEnabled;

    /**
     * 软删除标志
     */
    private Boolean isDeleted;
}