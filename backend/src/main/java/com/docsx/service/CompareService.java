package com.docsx.service;

import com.docsx.model.dto.R;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 比对服务 - 编排层
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@Service
public class CompareService {

    public R<Map<String, Object>> createCompareTask(
            MultipartFile fileA, MultipartFile fileB, String requestJson,
            String appId, String timestamp, String nonce, String sign) {
        // TODO: Phase 2 实现
        return R.ok(Map.of("message", "Compare service not yet implemented"));
    }
}
