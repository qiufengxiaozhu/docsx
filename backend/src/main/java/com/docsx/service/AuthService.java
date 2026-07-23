package com.docsx.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.docsx.mapper.SysUserMapper;
import com.docsx.model.entity.SysUser;
import com.docsx.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@Service
public class AuthService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    /**
     * 登录认证
     *
     * @param username 用户名
     * @param password 密码
     * @return JWT token，null 表示失败
     */
    public String login(String username, String password) {
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) {
            return null;
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }
        if (user.getStatus() != null && user.getStatus() != 1) {
            return null;
        }
        return jwtUtils.generateToken(user.getUsername());
    }
}
