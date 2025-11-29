package com.gemini.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限/菜单资源表 (sys_permission)
 */
@Data
@TableName("sys_permission")
public class SysPermission {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 父权限ID */
    private Long parentId;

    /** 菜单/权限名称 */
    private String name;

    /** 类型 (1:目录, 2:菜单, 3:按钮/API) */
    private Integer type;

    private Integer sortOrder;

    // --- 前端相关字段 ---
    /** 路由路径 */
    private String path;

    /** 前端组件路径 */
    private String component;

    /** 菜单图标 */
    private String icon;

    /** 是否缓存 (KeepAlive) */
    private Integer isCache;

    /** 是否可见 (前端菜单) */
    private Integer isVisible;

    // --- 后端权限相关字段 ---
    /** 权限标识符 (如: sys:user:add) */
    private String perms;

    // --- 通用字段 ---
    private LocalDateTime createdTime;
}