package com.gemini.admin.model;

import com.gemini.admin.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections; // 用于返回空的权限列表

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser implements UserDetails {

    private SysUser user;
    // 用于在 Redis 中存储和查找的唯一 Key
    private String tokenKey;
    // 用户拥有的权限标识符 (如: sys:user:add)
    private Collection<GrantedAuthority> authorities;

    public LoginUser(SysUser user) {
        this.user = user;
        // 初始时不加载权限，或加载一个默认权限
        this.authorities = Collections.emptyList();
    }

    public Long getUserId() {
        return user.getId();
    }

    // --- UserDetails 接口实现 ---

    // Spring Security 权限列表
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 在 RBAC 完善前，可以返回一个空集合或一个默认角色
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // 返回数据库中存储的加密密码
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 账户状态检查，基于 sys_user.status 字段 (1:正常, 0:禁用)
    @Override
    public boolean isAccountNonLocked() { return user.getStatus() != null && user.getStatus() == 1; }
    @Override
    public boolean isEnabled() { return user.getStatus() != null && user.getStatus() == 1; }

    // 简化处理，假设不过期
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
}