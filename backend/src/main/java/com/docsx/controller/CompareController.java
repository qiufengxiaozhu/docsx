package com.docsx.controller;

import com.docsx.model.dto.R;
import com.docsx.service.CompareService;
import com.docsx.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 比对 API 控制器
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/compare")
public class CompareController {

    @Autowired
    private CompareService compareService;

    @Autowired
    private TaskService taskService;

    @PostMapping("/create")
    public R<Map<String, Object>> create(
            @RequestParam("fileA") MultipartFile fileA,
            @RequestParam("fileB") MultipartFile fileB,
            @RequestPart(value = "request", required = false) String requestJson,
            @RequestHeader("X-App-Id") String appId,
            @RequestHeader("X-Timestamp") String timestamp,
            @RequestHeader("X-Nonce") String nonce,
            @RequestHeader("X-Sign") String sign) {
        return compareService.createCompareTask(fileA, fileB, requestJson, appId, timestamp, nonce, sign);
    }

    @GetMapping("/status/{taskId}")
    public R<Map<String, Object>> status(@PathVariable String taskId) {
        return taskService.getTaskStatus(taskId);
    }

    @GetMapping("/result/{sessionId}")
    public R<Map<String, Object>> result(@PathVariable String sessionId) {
        return taskService.getTaskResult(sessionId);
    }
}
