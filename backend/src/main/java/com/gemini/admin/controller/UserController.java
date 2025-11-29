package com.gemini.admin.controller;

import com.gemini.admin.common.ApiResponse;
import com.gemini.admin.model.LoginUser;
import com.gemini.admin.entity.SysUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户信息控制器，所有接口默认受 JWT 保护
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 获取当前登录用户信息的接口
     * URL: GET /api/user/info
     * 需要携带有效的 JWT Token 才能访问。
     */
    @GetMapping("/info")
    public ApiResponse<SysUser> getUserInfo() {

        // 1. 从 SecurityContextHolder 中获取当前的认证对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. 检查认证对象是否存在且已认证 (在 JWT 过滤器成功执行后，这里会存在)
        if (authentication == null || !authentication.isAuthenticated()) {
            // 理论上，如果 Spring Security 配置正确，这里不会被匿名用户访问到，
            // 因为会被 AuthExceptionHandler 拦截并返回 401。
            return ApiResponse.unauthorized("未找到认证信息");
        }

        // 3. 获取主体 (Principal) 对象，即我们的 LoginUser
        // 在 JwtAuthenticationTokenFilter 中，我们将 LoginUser 设置为 Principal
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) principal;

            // 为了安全，返回前清除敏感信息 (密码)
            SysUser user = loginUser.getUser();
            user.setPassword(null);

            return ApiResponse.success(user);
        }

        // 理论上，不应该走到这里
        return ApiResponse.failed(500, "认证主体类型错误");
    }
}