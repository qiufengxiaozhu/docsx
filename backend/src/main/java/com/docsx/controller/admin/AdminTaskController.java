package com.docsx.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.docsx.model.dto.R;
import com.docsx.model.entity.CompareTask;
import com.docsx.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理后台 - 任务管理接口
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/api/tasks")
public class AdminTaskController {

    private final TaskService taskService;

    public AdminTaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * 分页查询任务列表
     */
    @GetMapping
    public R<Page<CompareTask>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String appKey) {
        return R.ok(taskService.listTasks(page, size, status, appKey));
    }

    /**
     * 获取任务统计
     */
    @GetMapping("/stats")
    public R<TaskService.TaskStats> stats() {
        return R.ok(taskService.getStats());
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        // TODO: 可添加权限校验
        return R.ok("已删除");
    }
}
