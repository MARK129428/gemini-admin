package com.gemini.admin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gemini.admin.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    // 用于将 ApiResponse 对象序列化为 JSON 字符串
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 【处理 401 未认证】: 用户未登录 或 Token 无效/过期
     * 实现 AuthenticationEntryPoint 接口
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        // 1. 设置响应状态码为 401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 2. 告诉浏览器返回的是 JSON
        response.setContentType("application/json;charset=UTF-8");

        // 3. 使用您自定义的 ApiResponse.unauthorized() 方法封装结果
        ApiResponse<?> apiResponse = ApiResponse.unauthorized("认证失败，请携带有效的JWT令牌");

        // 4. 将 ApiResponse 写入响应体
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

    /**
     * 【处理 403 无权限】: 用户已登录，但没有访问该资源的权限
     * 实现 AccessDeniedHandler 接口
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        // 1. 设置响应状态码为 403
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        // 2. 告诉浏览器返回的是 JSON
        response.setContentType("application/json;charset=UTF-8");

        // 3. 使用您自定义的 ApiResponse.forbidden() 方法封装结果
        ApiResponse<?> apiResponse = ApiResponse.forbidden("权限不足，拒绝访问");

        // 4. 将 ApiResponse 写入响应体
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}