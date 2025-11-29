-- 1. 创建数据库 (如果不存在)
CREATE DATABASE IF NOT EXISTS `gemini_admin_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 切换到数据库
USE `gemini_admin_db`;

-- (可选：创建专有用户及授权，如果使用 root 或其他用户可跳过)
-- CREATE USER 'gemini'@'%' IDENTIFIED BY 'your_secure_password';
-- GRANT ALL PRIVILEGES ON `gemini_admin_db`.* TO 'gemini'@'%' WITH GRANT OPTION;
-- FLUSH PRIVILEGES;

-- ==========================================================
-- 3. 核心表结构 (先删除旧表，确保初始化干净)
-- ==========================================================

-- A. 用户与角色表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`     VARCHAR(64)  NOT NULL COMMENT '登录用户名',
    `password`     VARCHAR(128) NOT NULL COMMENT '加密后的密码',
    `nickname`     VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '用户昵称',
    `email`        VARCHAR(128) NULL COMMENT '邮箱',
    `phone`        VARCHAR(32)  NULL COMMENT '手机号码',
    `avatar`       VARCHAR(255) NULL COMMENT '头像URL',
    `status`       TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 (1:正常, 0:禁用)',

    -- 通用字段
    `created_by`   BIGINT       NOT NULL DEFAULT 0 COMMENT '创建人ID',
    `created_time` DATETIME     NOT NULL COMMENT '创建时间',
    `updated_by`   BIGINT       NOT NULL DEFAULT 0 COMMENT '更新人ID',
    `updated_time` DATETIME     NOT NULL COMMENT '更新时间',
    `is_deleted`   TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 (0:未删)',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    INDEX `idx_phone` (`phone`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户信息表';

DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_name`    VARCHAR(64)  NOT NULL COMMENT '角色名称',
    `role_key`     VARCHAR(64)  NOT NULL COMMENT '角色权限标识',
    `description`  VARCHAR(255) NULL COMMENT '角色描述',
    `sort_order`   INT          NOT NULL DEFAULT 0 COMMENT '显示顺序',

    -- 通用字段
    `created_time` DATETIME     NOT NULL COMMENT '创建时间',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='角色信息表';

DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',

    PRIMARY KEY (`user_id`, `role_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户-角色关联表';

-- B. 权限与菜单表
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `parent_id`    BIGINT       NOT NULL DEFAULT 0 COMMENT '父权限ID',
    `name`         VARCHAR(64)  NOT NULL COMMENT '菜单/权限名称',
    `type`         TINYINT      NOT NULL COMMENT '类型 (1:目录, 2:菜单, 3:按钮/API)',
    `sort_order`   INT          NOT NULL DEFAULT 0 COMMENT '显示顺序',

    -- 前端相关字段
    `path`         VARCHAR(255) NULL COMMENT '路由路径',
    `component`    VARCHAR(255) NULL COMMENT '前端组件路径',
    `icon`         VARCHAR(64)  NULL COMMENT '菜单图标',
    `is_cache`     TINYINT      NOT NULL DEFAULT 0 COMMENT '是否缓存 (KeepAlive)',
    `is_visible`   TINYINT      NOT NULL DEFAULT 1 COMMENT '是否可见 (前端菜单)',

    -- 后端权限相关字段
    `perms`        VARCHAR(128) NULL COMMENT '权限标识符 (如: sys:user:add)',

    -- 通用字段
    `created_time` DATETIME     NOT NULL COMMENT '创建时间',

    PRIMARY KEY (`id`),
    INDEX `idx_parent_id` (`parent_id`),
    INDEX `idx_perms` (`perms`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='权限/菜单资源表';

DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`
(
    `role_id`       BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限/菜单ID',

    PRIMARY KEY (`role_id`, `permission_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='角色-权限关联表';