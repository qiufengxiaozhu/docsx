package com.docsx.service;

import com.docsx.model.dto.R;
import com.docsx.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@Service
public class AuthService {

    @Autowired
    private JwtUtils jwtUtils;

    public R<Map<String, String>> login(String username, String password) {
        // TODO: 校验用户名密码
        if ("admin".equals(username) && "admin123".equals(password)) {
            String token = jwtUtils.generateToken(username);
            return R.ok(Map.of("token", token));
        }
        return R.fail(401, "用户名或密码错误");
    }
}
