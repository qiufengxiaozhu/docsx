package com.docsx.controller;

import com.docsx.model.dto.R;
import com.docsx.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理后台认证控制器
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public R<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            return R.fail("用户名和密码不能为空");
        }

        String token = authService.login(username, password);
        if (token == null) {
            return R.fail(401, "用户名或密码错误");
        }

        return R.ok(Map.of("token", token));
    }
}
