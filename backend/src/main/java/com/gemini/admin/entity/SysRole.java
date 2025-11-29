package com.gemini.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色信息表 (sys_role)
 */
@Data
@TableName("sys_role")
public class SysRole {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String roleName;

    /** 角色权限标识 (如: ROLE_ADMIN) */
    private String roleKey;

    private String description;

    private Integer sortOrder;

    // --- 通用字段 ---
    private LocalDateTime createdTime;
}