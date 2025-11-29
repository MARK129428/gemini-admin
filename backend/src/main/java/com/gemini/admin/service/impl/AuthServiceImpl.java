package com.gemini.admin.service.impl;

import com.gemini.admin.model.LoginUser;
import com.gemini.admin.model.request.LoginRequest;
import com.gemini.admin.model.response.LoginResponse;
import com.gemini.admin.service.AuthService;
import com.gemini.admin.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Override
    public LoginResponse login(LoginRequest request) {

        // 1. 封装认证信息：将请求的用户名和密码包装成 Spring Security 要求的对象
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

        Authentication authentication;
        try {
            // 2. 核心认证调用：
            // 这一步会委托给 AuthenticationManager，它会调用 UserDetailsServiceImpl
            // 和 PasswordEncoder 来完成数据库查询和密码比对。
            authentication = authenticationManager.authenticate(authenticationToken);

        } catch (AuthenticationException e) {
            // 3. 认证失败处理：
            // 通常是用户名或密码不匹配，或账户被禁用等。
            throw new RuntimeException("登录失败：用户名或密码错误", e);
        }

        // 4. 认证成功，获取 LoginUser 对象：
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        // 5. 生成 JWT Token (内部会生成 UUID/TokenKey 并设置到 LoginUser)
        String jwt = tokenService.createToken(loginUser);

        // 6. 将完整的 LoginUser 对象（含权限）存入 Redis
        tokenService.setLoginUserToRedis(loginUser);

        // 7. 构建并返回响应：
        return LoginResponse.builder()
                .token(jwt)
                .userId(loginUser.getUserId())
                .nickname(loginUser.getUser().getNickname())
                .build();
    }
}