package com.docsx.controller;

import com.docsx.model.entity.CompareTask;
import com.docsx.service.CompareService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 比对结果页面控制器（Thymeleaf 渲染）
 * 供三方系统通过 iframe 嵌入
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@Controller
public class ViewerController {

    private final CompareService compareService;

    public ViewerController(CompareService compareService) {
        this.compareService = compareService;
    }

    /**
     * 比对结果页面
     * GET /view/{sessionId}
     */
    @GetMapping("/view/{sessionId}")
    public String viewer(@PathVariable String sessionId, Model model) {
        CompareTask task = compareService.getTaskBySessionId(sessionId);
        if (task == null) {
            model.addAttribute("error", "任务不存在");
            return "compare-viewer";
        }

        model.addAttribute("sessionId", sessionId);
        model.addAttribute("status", task.getStatus());
        model.addAttribute("fileName1", task.getFileName1());
        model.addAttribute("fileName2", task.getFileName2());

        if ("COMPLETED".equals(task.getStatus())) {
            model.addAttribute("resultJson", task.getResultJson());
        } else if ("FAILED".equals(task.getStatus())) {
            model.addAttribute("error", task.getErrorMessage());
        }

        return "compare-viewer";
    }
}
