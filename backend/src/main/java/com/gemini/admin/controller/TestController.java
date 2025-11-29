package com.gemini.admin.controller;

import com.gemini.admin.common.ApiResponse;
import com.gemini.admin.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 数据库和 Redis 连接测试接口
 * 访问路径: /api/connect
 */
@RestController
@RequestMapping("/connect")
@RequiredArgsConstructor // 使用 Lombok 自动生成 final 字段的构造函数注入
@Slf4j
public class TestController {

    // 注入用户 Service (用于测试 MySQL 连接)
    private final SysUserService sysUserService;

    // 注入 Redis 客户端 (用于测试 Redis 连接)
    private final StringRedisTemplate redisTemplate;

    /**
     * GET /api/connect/mysql
     * 测试 MySQL 数据库连接和查询。
     */
    @GetMapping("/mysql")
    public ApiResponse<Void> testMysqlConnection() {
        try {
            // 尝试从数据库查询 ID 为 1 的用户 (假设 admin 用户 ID 是 1)
            // 如果连接失败或用户不存在，会抛出异常
            String username = sysUserService.getById(1L).getUsername();

            String successMsg = String.format("MySQL 连接成功！成功查询到用户ID=1，用户名为: %s", username);
            log.info(successMsg);
            return ApiResponse.success(successMsg);

        } catch (Exception e) {
            log.error("MySQL 连接或查询失败：{}", e.getMessage(), e);
            // 抛出运行时异常，交给全局异常处理器捕获
            throw new RuntimeException("MySQL 连接或查询失败: " + e.getMessage());
        }
    }

    /**
     * GET /api/connect/redis
     * 测试 Redis 连接和读写操作。
     */
    @GetMapping("/redis")
    public ApiResponse<Void> testRedisConnection() {
        String key = "test:connection:" + System.currentTimeMillis();
        String value = "success_time_" + LocalDateTime.now();

        try {
            // 写入 Redis，并设置 10 秒过期时间
            redisTemplate.opsForValue().set(key, value, 10, TimeUnit.SECONDS);

            // 读取 Redis
            String retrievedValue = redisTemplate.opsForValue().get(key);

            if (value.equals(retrievedValue)) {
                String successMsg = String.format("Redis 连接成功！成功写入和读取 Key: %s, Value: %s", key, retrievedValue);
                log.info(successMsg);
                return ApiResponse.success(successMsg);
            } else {
                throw new Exception("写入值与读取值不匹配。");
            }
        } catch (Exception e) {
            log.error("Redis 连接或操作失败：{}", e.getMessage(), e);
            // 抛出运行时异常，交给全局异常处理器捕获
            throw new RuntimeException("Redis 连接或操作失败: " + e.getMessage());
        }
    }
}