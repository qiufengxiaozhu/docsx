package com.docsx.security;

import cn.hutool.crypto.digest.DigestUtil;
import org.springframework.stereotype.Component;

/**
 * HMAC-MD5 签名验证器（三方接入认证）
 * 签名算法：MD5(app_secret + "@@" + timestamp + "@@" + nonce)
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@Component
public class HmacVerifier {

    private static final long TIMESTAMP_TOLERANCE_MS = 5 * 60 * 1000;

    public boolean verify(String appSecret, String timestamp, String nonce, String sign) {
        if (appSecret == null || timestamp == null || nonce == null || sign == null) {
            return false;
        }
        long ts = Long.parseLong(timestamp);
        long now = System.currentTimeMillis() / 1000;
        if (Math.abs(now - ts) > TIMESTAMP_TOLERANCE_MS / 1000) {
            return false;
        }
        String expected = DigestUtil.md5Hex(appSecret + "@@" + timestamp + "@@" + nonce);
        return expected.equalsIgnoreCase(sign);
    }
}
