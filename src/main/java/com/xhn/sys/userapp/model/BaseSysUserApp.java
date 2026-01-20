package com.xhn.sys.userapp.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户应用关联表实体类
 * 
 * @author xhn
 * @date 2025-12-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_user_app")
public class BaseSysUserApp implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID，对应 sys_user.id
     */
    private Long userId;

    /**
     * 系统ID，对应 sys_app.id
     */
    private Long appId;

    /**
     * 访问状态：1允许访问 0禁止访问
     */
    private Integer status;

    /**
     * 授权创建时间
     */
    private LocalDateTime createTime;

}