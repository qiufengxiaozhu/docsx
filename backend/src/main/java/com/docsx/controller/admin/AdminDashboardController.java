package com.docsx.controller.admin;

import com.docsx.model.dto.R;
import com.docsx.mapper.AppMapper;
import com.docsx.mapper.CompareTaskMapper;
import com.docsx.mapper.FontMapper;
import com.docsx.service.TaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 管理后台 - 仪表盘接口
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/api/dashboard")
public class AdminDashboardController {

    private final TaskService taskService;
    private final AppMapper appMapper;
    private final FontMapper fontMapper;

    public AdminDashboardController(TaskService taskService, AppMapper appMapper, FontMapper fontMapper) {
        this.taskService = taskService;
        this.appMapper = appMapper;
        this.fontMapper = fontMapper;
    }

    /**
     * 仪表盘概览数据
     */
    @GetMapping
    public R<Map<String, Object>> overview() {
        TaskService.TaskStats stats = taskService.getStats();
        long appCount = appMapper.selectCount(null);
        long fontCount = fontMapper.selectCount(null);

        return R.ok(Map.of(
                "taskTotal", stats.total,
                "taskPending", stats.pending,
                "taskRunning", stats.running,
                "taskCompleted", stats.completed,
                "taskFailed", stats.failed,
                "appCount", appCount,
                "fontCount", fontCount
        ));
    }
}
