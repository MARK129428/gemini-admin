package com.gemini.admin.service;

import com.gemini.admin.model.request.LoginRequest;
import com.gemini.admin.model.response.LoginResponse;

public interface AuthService {

    /**
     * 用户登录认证并生成令牌
     */
    LoginResponse login(LoginRequest request);
}