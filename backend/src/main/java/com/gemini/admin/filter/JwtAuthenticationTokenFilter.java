package com.gemini.admin.filter;

import com.gemini.admin.model.LoginUser;
import com.gemini.admin.service.TokenService;
// ... (其他导入保持不变)
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_TYPE = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = request.getHeader(AUTH_HEADER);

        if (StringUtils.hasText(token) && token.startsWith(AUTH_TYPE)) {
            token = token.substring(AUTH_TYPE.length());

            try {
                Claims claims = tokenService.parseToken(token);
                String tokenKey = claims.get("uuid", String.class);

                LoginUser loginUser = tokenService.getLoginUserFromRedis(tokenKey);

                if (loginUser != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

                    // 将认证信息放入 SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }

            } catch (Exception e) {
                // Token 无效，让 Security 链处理
            }
        }
        filterChain.doFilter(request, response);
    }
}