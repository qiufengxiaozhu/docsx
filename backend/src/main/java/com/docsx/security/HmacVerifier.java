package com.docsx.security;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    private static final long TIMESTAMP_TOLERANCE_SECONDS = 5 * 60;

    public boolean verify(String appSecret, String timestamp, String nonce, String sign) {
        if (appSecret == null || timestamp == null || nonce == null || sign == null) {
            return false;
        }
        try {
            long ts = Long.parseLong(timestamp);
            long now = System.currentTimeMillis() / 1000;
            if (Math.abs(now - ts) > TIMESTAMP_TOLERANCE_SECONDS) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        String expected = md5Hex(appSecret + "@@" + timestamp + "@@" + nonce);
        return expected.equalsIgnoreCase(sign);
    }

    private String md5Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }
}
