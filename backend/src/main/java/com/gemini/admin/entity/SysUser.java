package com.gemini.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息表 (sys_user)
 */
@Data
@TableName("sys_user")
public class SysUser {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String username;

    // 密码字段，注意安全，不应轻易暴露
    private String password;

    private String nickname;

    private String email;

    private String phone;

    private String avatar;

    /** 状态 (1:正常, 0:禁用) */
    private Integer status;

    // --- 通用字段 ---
    @TableField("created_by")
    private Long createdBy;

    @TableField("created_time")
    private LocalDateTime createdTime;

    @TableField("updated_by")
    private Long updatedBy;

    @TableField("updated_time")
    private LocalDateTime updatedTime;

    /** 逻辑删除标记 (0:未删, 1:已删) */
    @TableField("is_deleted")
    private Integer isDeleted;
}