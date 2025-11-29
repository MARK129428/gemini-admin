package com.gemini.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * Gemini Admin 项目主启动类
 */
@SpringBootApplication(
        exclude = {
                SecurityAutoConfiguration.class // 排除 Spring Security 默认配置
                // ManagementWebSecurityAutoConfiguration.class // 如果有 actuator，也排除
        }
)
@MapperScan("com.gemini.admin.mapper")
public class GeminiAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeminiAdminApplication.class, args);
        System.out.println("----------------------------------------------");
        System.out.println("       Gemini Admin 后端应用启动成功！         ");
        System.out.println("       Swagger 地址: http://localhost:8080/api/swagger-ui/index.html ");
        System.out.println("----------------------------------------------");
    }

}