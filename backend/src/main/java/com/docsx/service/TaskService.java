package com.docsx.service;

import com.docsx.model.dto.R;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 任务池管理服务
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@Service
public class TaskService {

    public R<Map<String, Object>> getTaskStatus(String taskId) {
        // TODO: Phase 2 实现
        return R.ok(Map.of("taskId", taskId, "status", "pending"));
    }

    public R<Map<String, Object>> getTaskResult(String sessionId) {
        // TODO: Phase 2 实现
        return R.ok(Map.of("sessionId", sessionId, "status", "pending"));
    }
}
