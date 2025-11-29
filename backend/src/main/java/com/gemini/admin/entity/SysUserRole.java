package com.gemini.admin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户-角色关联表 (sys_user_role)
 */
@Data
@TableName("sys_user_role")
public class SysUserRole implements Serializable {

    // MyBatis-Plus 默认需要一个主键，尽管这里是联合主键，
    // 但通常我们会使用 @TableId(type = IdType.NONE) 或不标注。

    /** 用户ID */
    @TableId
    private Long userId;

    /** 角色ID */
    private Long roleId;
}