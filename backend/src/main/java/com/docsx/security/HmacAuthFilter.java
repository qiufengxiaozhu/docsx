package com.docsx.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.docsx.mapper.AppMapper;
import com.docsx.model.entity.App;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * HMAC 签名认证过滤器
 * 拦截 /api/compare/** 请求，验证三方签名
 *
 * 签名参数从请求参数/Header 中获取：
 * - X-App-Key / appKey
 * - X-Timestamp / timestamp
 * - X-Nonce / nonce
 * - X-Sign / sign
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
public class HmacAuthFilter extends OncePerRequestFilter {

    private final AppMapper appMapper;
    private final HmacVerifier hmacVerifier;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public HmacAuthFilter(AppMapper appMapper, HmacVerifier hmacVerifier) {
        this.appMapper = appMapper;
        this.hmacVerifier = hmacVerifier;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // 只拦截 /api/compare 路径
        if (!path.startsWith("/api/compare")) {
            filterChain.doFilter(request, response);
            return;
        }

        String appKey = getParam(request, "X-App-Key", "appKey");
        String timestamp = getParam(request, "X-Timestamp", "timestamp");
        String nonce = getParam(request, "X-Nonce", "nonce");
        String sign = getParam(request, "X-Sign", "sign");

        // 如果没有签名参数，跳过验证（开发模式）
        if (appKey == null && sign == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (appKey == null || sign == null) {
            sendError(response, 401, "缺少签名参数");
            return;
        }

        // 查询 app
        App app = appMapper.selectOne(
                new LambdaQueryWrapper<App>().eq(App::getAppKey, appKey).eq(App::getStatus, 1));
        if (app == null) {
            sendError(response, 401, "无效的 appKey");
            return;
        }

        // 验证签名
        if (!hmacVerifier.verify(app.getAppSecret(), timestamp, nonce, sign)) {
            sendError(response, 401, "签名验证失败");
            return;
        }

        // 将 appKey 放入请求属性
        request.setAttribute("appKey", appKey);
        filterChain.doFilter(request, response);
    }

    private String getParam(HttpServletRequest request, String headerName, String paramName) {
        String val = request.getHeader(headerName);
        if (val == null) {
            val = request.getParameter(paramName);
        }
        return val;
    }

    private void sendError(HttpServletResponse response, int status, String msg) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(
                Map.of("code", status, "msg", msg)));
    }
}
