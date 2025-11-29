package com.gemini.admin.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gemini.admin.model.LoginUser;
import com.gemini.admin.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TokenServiceImpl implements TokenService {

    // ------------------------------------
    // 1. 字段和配置注入
    // ------------------------------------
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire-time}")
    private long expireTime;

    @Value("${jwt.user-id-key}")
    private String userIdKey;

    @Value("${redis.prefix}")
    private String redisPrefix;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ------------------------------------
    // 2. 核心私有方法
    // ------------------------------------

    /**
     * 获取用于 JWT 签名的 SecretKey
     */
    private SecretKey getSecretKey() {
        // 使用 Base64 解码配置的密钥
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    /**
     * 构建 Redis Key
     */
    private String getRedisKey(String tokenKey) {
        return redisPrefix + tokenKey;
    }

    // ------------------------------------
    // 3. 接口实现
    // ------------------------------------

    @Override
    public String createToken(LoginUser loginUser) {
        // 1. 生成唯一的 Key，用于在 Redis 中存储
        String tokenKey = UUID.randomUUID().toString().replace("-", "");
        loginUser.setTokenKey(tokenKey);

        // 2. 设置 JWT Claims (Payload)
        Map<String, Object> claims = new HashMap<>();
        // 将 Redis Key ("uuid") 和用户 ID 放入 Claims
        claims.put("uuid", tokenKey);
        claims.put(userIdKey, loginUser.getUserId());

        long now = System.currentTimeMillis();
        Date expiration = new Date(now + expireTime);

        // 3. 构建并签名 JWT
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(expiration)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public void setLoginUserToRedis(LoginUser loginUser) {
        if (loginUser.getTokenKey() == null) {
            throw new IllegalArgumentException("LoginUser 必须包含 TokenKey/UUID");
        }
        String key = getRedisKey(loginUser.getTokenKey());

        try {
            // 序列化为 JSON 字符串存储
            String json = objectMapper.writeValueAsString(loginUser);
            // 设置过期时间，与 JWT 过期时间一致
            redisTemplate.opsForValue().set(key, json, expireTime, TimeUnit.MILLISECONDS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 存储 LoginUser 序列化失败", e);
        }
    }

    @Override
    public LoginUser getLoginUserFromRedis(String tokenKey) {
        String key = getRedisKey(tokenKey);
        String json = redisTemplate.opsForValue().get(key);

        if (json != null) {
            try {
                // 反序列化为 LoginUser 对象
                return objectMapper.readValue(json, LoginUser.class);
            } catch (JsonProcessingException e) {
                System.err.println("Redis 反序列化 LoginUser 失败: " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    @Override
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public void deleteLoginUser(String tokenKey) {
        String key = getRedisKey(tokenKey);
        redisTemplate.delete(key);
    }
}