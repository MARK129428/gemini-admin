package com.gemini.admin.controller;

import com.gemini.admin.model.request.LoginRequest;
import com.gemini.admin.model.response.LoginResponse;
import com.gemini.admin.service.AuthService;
import com.gemini.admin.service.TokenService;
import com.gemini.admin.common.ApiResponse; // 导入您的统一响应类
import com.gemini.admin.model.LoginUser;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenService tokenService;

    /**
     * 登录接口：POST /api/auth/login
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // 调用服务层进行认证和 Token 生成
        LoginResponse response = authService.login(request);

        // 使用您的 ApiResponse.success() 封装成功结果
        return ApiResponse.success(response);
    }

    /**
     * 登出接口：POST /api/auth/logout (需Token保护)
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        try {
            // 确保获取的是已认证用户 (只有 JWT 过滤器生效后才能正确获取)
            LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String tokenKey = loginUser.getTokenKey();

            // 删除 Redis 记录并清除上下文
            tokenService.deleteLoginUser(tokenKey);
            SecurityContextHolder.clearContext();

        } catch (Exception e) {
            // 生产环境中，此处应记录日志。
            // 即使登出操作失败（如用户未登录），也应返回成功，不暴露内部错误。
            return ApiResponse.success("登出处理完毕");
        }

        return ApiResponse.success("登出成功");
    }


}