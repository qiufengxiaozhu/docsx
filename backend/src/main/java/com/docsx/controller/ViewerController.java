package com.docsx.controller;

import com.docsx.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 比对结果页面控制器
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@Controller
public class ViewerController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/view/{sessionId}")
    public String compareViewer(@PathVariable String sessionId, Model model) {
        model.addAttribute("sessionId", sessionId);
        return "compare-viewer";
    }
}
