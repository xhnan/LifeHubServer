package com.xhn.sys.permissionapi.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 权限接口关联表实体类
 * @author xhn
 * @date 2025-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_permission_api")
public class BaseSysPermissionApi {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联权限ID
     */
    private Long permissionId;

    /**
     * 接口所属系统编码
     */
    private String systemCode;

    /**
     * HTTP方法，如GET/POST
     */
    private String httpMethod;

    /**
     * 接口URL匹配模式
     */
    private String urlPattern;

    /**
     * 接口版本号
     */
    private String apiVersion;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;

    /**
     * 是否需要权限校验
     */
    private Boolean needAuth;

    /**
     * 请求内容类型
     */
    private String requestContentType;

    /**
     * 返回内容类型
     */
    private String responseContentType;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

}