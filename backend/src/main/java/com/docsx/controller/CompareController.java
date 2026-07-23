package com.docsx.controller;

import com.docsx.model.dto.R;
import com.docsx.model.entity.CompareTask;
import com.docsx.service.CompareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

/**
 * 比对 API 控制器
 * 提供给三方系统通过 HMAC 签名认证后调用
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/compare")
public class CompareController {

    private static final Logger log = LoggerFactory.getLogger(CompareController.class);

    private final CompareService compareService;

    public CompareController(CompareService compareService) {
        this.compareService = compareService;
    }

    /**
     * 创建比对任务（文件上传方式）
     *
     * POST /api/compare/create
     * @param file1 文档A
     * @param file2 文档B
     * @param appKey 应用 Key（从认证头中解析）
     * @return sessionId 用于查询状态和获取结果页
     */
    @PostMapping("/create")
    public R<Map<String, String>> create(
            @RequestParam("file1") MultipartFile file1,
            @RequestParam("file2") MultipartFile file2,
            @RequestParam(value = "appKey", required = false) String appKey,
            @RequestParam(value = "callbackUrl", required = false) String callbackUrl) {

        if (file1.isEmpty() || file2.isEmpty()) {
            return R.fail("文件不能为空");
        }

        try {
            // 保存上传文件到临时目录
            Path tmpDir = Files.createTempDirectory("docsx_");
            String fileName1 = sanitizeFileName(file1.getOriginalFilename());
            String fileName2 = sanitizeFileName(file2.getOriginalFilename());

            Path path1 = tmpDir.resolve(UUID.randomUUID().toString().substring(0, 8) + "_" + fileName1);
            Path path2 = tmpDir.resolve(UUID.randomUUID().toString().substring(0, 8) + "_" + fileName2);
            file1.transferTo(path1.toFile());
            file2.transferTo(path2.toFile());

            // 创建任务
            String sessionId = compareService.createTask(
                    appKey, path1.toString(), path2.toString(),
                    fileName1, fileName2
            );

            // 异步执行比对
            compareService.executeCompare(sessionId);

            log.info("Compare task created: {} ({} vs {})", sessionId, fileName1, fileName2);

            return R.ok(Map.of(
                    "sessionId", sessionId,
                    "viewUrl", "/view/" + sessionId
            ));
        } catch (IOException e) {
            log.error("Failed to create compare task", e);
            return R.fail("文件保存失败: " + e.getMessage());
        }
    }

    /**
     * 查询任务状态
     *
     * GET /api/compare/status/{sessionId}
     */
    @GetMapping("/status/{sessionId}")
    public R<Map<String, Object>> status(@PathVariable String sessionId) {
        CompareTask task = compareService.getTaskBySessionId(sessionId);
        if (task == null) {
            return R.fail("任务不存在");
        }

        Map<String, Object> data = Map.of(
                "sessionId", task.getSessionId(),
                "status", task.getStatus(),
                "fileName1", task.getFileName1() != null ? task.getFileName1() : "",
                "fileName2", task.getFileName2() != null ? task.getFileName2() : "",
                "createdAt", task.getCreatedAt() != null ? task.getCreatedAt().toString() : ""
        );
        return R.ok(data);
    }

    /**
     * 获取比对结果 JSON
     *
     * GET /api/compare/result/{sessionId}
     */
    @GetMapping("/result/{sessionId}")
    public R<Object> result(@PathVariable String sessionId) {
        CompareTask task = compareService.getTaskBySessionId(sessionId);
        if (task == null) {
            return R.fail("任务不存在");
        }
        if (!"COMPLETED".equals(task.getStatus())) {
            return R.fail("任务未完成，当前状态: " + task.getStatus());
        }
        try {
            Object result = new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(task.getResultJson(), Object.class);
            return R.ok(result);
        } catch (Exception e) {
            return R.fail("解析结果失败");
        }
    }

    private String sanitizeFileName(String filename) {
        if (filename == null) return "unknown";
        return filename.replaceAll("[^a-zA-Z0-9.\\-_\\u4e00-\\u9fa5]", "_");
    }
}
