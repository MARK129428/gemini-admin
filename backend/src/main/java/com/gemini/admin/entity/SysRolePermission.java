package com.gemini.admin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色-权限关联表 (sys_role_permission)
 */
@Data
@TableName("sys_role_permission")
public class SysRolePermission implements Serializable {

    /** 角色ID */
    @TableId
    private Long roleId;

    /** 权限/菜单ID */
    private Long permissionId;
}