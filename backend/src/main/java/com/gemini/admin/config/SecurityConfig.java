package com.gemini.admin.config;

import com.gemini.admin.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // 正确的参照过滤器

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 【1. 注入我们创建的两个核心组件】
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Autowired
    private AuthExceptionHandler authExceptionHandler; // 异常处理器

    // ------------------------------------
    // Spring Security 核心 Bean 定义
    // ------------------------------------

    /**
     * 定义密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 暴露 AuthenticationManager 实例，供 AuthService 调用
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // ------------------------------------
    // 安全配置链
    // ------------------------------------

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF，因为我们使用 JWT 是无状态的
                .csrf(AbstractHttpConfigurer::disable)

                // 基于 Token，不需要 Session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 【2. 整合异常处理器：处理 401 和 403 错误，并返回 JSON】
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(authExceptionHandler) // 401 未认证
                        .accessDeniedHandler(authExceptionHandler)      // 403 无权限
                )

                // 配置权限规则
                .authorizeHttpRequests(auth -> auth
                        // 允许匿名访问登录接口 (注意您的 Context Path 是 /api)
                        .requestMatchers("/api/auth/login", "/api/user/register").permitAll()
                        // 允许访问静态资源等
                        .requestMatchers("/api/static/**", "/api/swagger-ui/**", "/api/v3/api-docs/**").permitAll()
                        // 任何其他请求必须认证
                        .anyRequest().authenticated()
                );

        // 【3. 整合 JWT 过滤器】
        // 将自定义的 JWT 过滤器插入到 UsernamePasswordAuthenticationFilter 之前
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}