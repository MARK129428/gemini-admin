package com.gemini.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gemini.admin.entity.SysUser;

/**
 * 系统用户服务接口
 * 继承 MyBatis-Plus 的 IService 接口
 */
public interface SysUserService extends IService<SysUser> {
    // 这里可以定义其他自定义方法，目前只使用 IService 提供的 getById(Long id)
    SysUser getById(Long id);

}