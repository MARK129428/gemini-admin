package com.gemini.admin.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token; // JWT 令牌
    private Long userId;
    private String nickname;
    // ... 可以添加其他需要返回给前端的非敏感信息
}