package com.gemini.admin.service;

import com.gemini.admin.model.LoginUser;
import io.jsonwebtoken.Claims;

public interface TokenService {

    /**
     * 生成 JWT 令牌，并返回包含 TokenKey 的 LoginUser
     * @param loginUser 已认证的用户信息
     * @return JWT 字符串
     */
    String createToken(LoginUser loginUser);

    /**
     * 将 LoginUser 存储到 Redis
     */
    void setLoginUserToRedis(LoginUser loginUser);

    /**
     * 从 Redis 获取 LoginUser
     */
    LoginUser getLoginUserFromRedis(String tokenKey);

    /**
     * 解析 JWT Token，获取 Claims
     */
    Claims parseToken(String token);

    // 登出方法（可选）
    void deleteLoginUser(String tokenKey);
}