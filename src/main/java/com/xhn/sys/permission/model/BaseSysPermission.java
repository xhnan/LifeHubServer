package com.xhn.sys.permission.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限实体类
 * 
 * @author xhn
 * @date 2025-12-19
 */
@Data
@TableName("sys_permission")
public class BaseSysPermission {
    
    /**
     * 权限ID，唯一标识权限
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 权限标识符，如 order_system:page:orders:view
     */
    private String permissionKey;
    
    /**
     * 权限名称，如 查看订单
     */
    private String name;
    
    /**
     * 权限描述，详细说明该权限的用途
     */
    private String description;
    
    /**
     * 权限类型（如 页面、按钮、API等），用于区分权限的种类
     */
    private String type;
    
    /**
     * 应用编码，标识该权限属于哪个应用
     */
    private String appCode;
    
    /**
     * 权限状态，1为启用，0为禁用
     */
    private Short status;
    
    /**
     * 父级权限ID，用于构建树形结构的权限
     */
    private Integer parentId;
    
    /**
     * 是否已删除（0为未删除，1为已删除），用于软删除
     */
    private Short isDeleted;
    
    /**
     * 权限优先级，用于排序或处理权限
     */
    private Integer priority;
    
    /**
     * 是否为系统内置权限（0为否，1为是）
     */
    private Short isSystem;
    
    /**
     * 权限创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 权限最后更新时间
     */
    private LocalDateTime updatedAt;
}