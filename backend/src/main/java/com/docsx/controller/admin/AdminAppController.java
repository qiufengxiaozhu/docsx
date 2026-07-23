package com.docsx.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.docsx.model.dto.R;
import com.docsx.model.entity.App;
import com.docsx.mapper.AppMapper;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * 管理后台 - 应用管理接口
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/api/apps")
public class AdminAppController {

    private final AppMapper appMapper;

    public AdminAppController(AppMapper appMapper) {
        this.appMapper = appMapper;
    }

    /**
     * 分页查询应用列表
     */
    @GetMapping
    public R<Page<App>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        LambdaQueryWrapper<App> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(App::getCreatedAt);
        return R.ok(appMapper.selectPage(new Page<>(page, size), wrapper));
    }

    /**
     * 创建应用
     */
    @PostMapping
    public R<App> create(@RequestBody App app) {
        app.setAppKey(generateKey());
        app.setAppSecret(generateSecret());
        app.setStatus(1);
        app.setCreatedAt(LocalDateTime.now());
        app.setUpdatedAt(LocalDateTime.now());
        appMapper.insert(app);
        return R.ok(app);
    }

    /**
     * 更新应用
     */
    @PutMapping("/{id}")
    public R<App> update(@PathVariable Long id, @RequestBody App app) {
        app.setId(id);
        app.setUpdatedAt(LocalDateTime.now());
        appMapper.updateById(app);
        return R.ok(app);
    }

    /**
     * 删除应用
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        appMapper.deleteById(id);
        return R.ok("已删除");
    }

    /**
     * 重新生成密钥
     */
    @PostMapping("/{id}/regenerate-secret")
    public R<String> regenerateSecret(@PathVariable Long id) {
        App app = appMapper.selectById(id);
        if (app == null) return R.fail("应用不存在");
        app.setAppSecret(generateSecret());
        app.setUpdatedAt(LocalDateTime.now());
        appMapper.updateById(app);
        return R.ok(app.getAppSecret());
    }

    private String generateKey() {
        return "dxk_" + randomHex(16);
    }

    private String generateSecret() {
        return "dxs_" + randomHex(32);
    }

    private String randomHex(int byteCount) {
        byte[] bytes = new byte[byteCount];
        new SecureRandom().nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
