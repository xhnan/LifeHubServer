package com.xhn.sys.role.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 角色信息表实体类
 * @author xhn
 * @date 2025-12-19
 */
@Data
@TableName("sys_role")
public class BaseSysRole {
    /**
     * 角色ID，主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 角色编码，如 ADMIN、USER
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色状态：1启用 0禁用
     */
    private Integer status;

    /**
     * 角色备注说明
     */
    private String remark;

    /**
     * 逻辑删除标志：0未删除 1已删除
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}