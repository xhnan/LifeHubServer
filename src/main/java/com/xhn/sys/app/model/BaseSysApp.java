package com.xhn.sys.app.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统应用实体类
 * @author xhn
 * @date 2025-01-22
 */
@Data
@TableName("sys_app")
public class BaseSysApp {

    /**
     * 系统ID，主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 系统编码，如 SSO、OMS、LIS
     */
    private String appCode;

    /**
     * 系统名称
     */
    private String appName;

    /**
     * 系统状态：1启用 0禁用
     */
    private Integer status;

    /**
     * 系统说明或备注
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