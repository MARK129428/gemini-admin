package com.gemini.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gemini.admin.entity.SysUser;
import com.gemini.admin.mapper.SysUserMapper;
import com.gemini.admin.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 查询用户信息
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        // 查询 username 且 is_deleted = 0 的用户
        wrapper.eq(SysUser::getUsername, username).eq(SysUser::getIsDeleted, 0);
        SysUser user = userMapper.selectOne(wrapper);

        if (user == null) {
            // Spring Security 要求在用户不存在时抛出 UsernameNotFoundException
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        // 2. 检查用户状态 (LoginUser 中的 isAccountNonLocked/isEnabled 会自动检查 status)
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用，无法登录");
        }

        // 3. TODO: 查询用户权限信息 (后续整合 sys_role_permission 时实现)

        // 4. 返回 LoginUser 对象
        return new LoginUser(user);
    }
}