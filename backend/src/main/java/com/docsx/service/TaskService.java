package com.docsx.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.docsx.model.entity.CompareTask;
import com.docsx.mapper.CompareTaskMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务管理服务
 * 负责任务列表查询、过期清理、统计等
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final CompareTaskMapper compareTaskMapper;

    public TaskService(CompareTaskMapper compareTaskMapper) {
        this.compareTaskMapper = compareTaskMapper;
    }

    /**
     * 分页查询任务
     */
    public Page<CompareTask> listTasks(int page, int size, String status, String appKey) {
        LambdaQueryWrapper<CompareTask> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(CompareTask::getStatus, status);
        }
        if (appKey != null && !appKey.isEmpty()) {
            wrapper.eq(CompareTask::getAppKey, appKey);
        }
        wrapper.orderByDesc(CompareTask::getCreatedAt);
        return compareTaskMapper.selectPage(new Page<>(page, size), wrapper);
    }

    /**
     * 统计各状态任务数
     */
    public TaskStats getStats() {
        TaskStats stats = new TaskStats();
        stats.total = compareTaskMapper.selectCount(null);
        stats.pending = countByStatus("PENDING");
        stats.running = countByStatus("RUNNING");
        stats.completed = countByStatus("COMPLETED");
        stats.failed = countByStatus("FAILED");
        return stats;
    }

    /**
     * 定时清理过期任务（每小时执行）
     */
    @Scheduled(fixedDelay = 3600000)
    public void cleanExpiredTasks() {
        LambdaQueryWrapper<CompareTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(CompareTask::getExpiresAt)
                .lt(CompareTask::getExpiresAt, LocalDateTime.now());

        List<CompareTask> expired = compareTaskMapper.selectList(wrapper);
        if (expired.isEmpty()) return;

        for (CompareTask task : expired) {
            compareTaskMapper.deleteById(task.getId());
        }
        log.info("Cleaned {} expired tasks", expired.size());
    }

    private long countByStatus(String status) {
        LambdaQueryWrapper<CompareTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CompareTask::getStatus, status);
        return compareTaskMapper.selectCount(wrapper);
    }

    public static class TaskStats {
        public long total;
        public long pending;
        public long running;
        public long completed;
        public long failed;
    }
}
