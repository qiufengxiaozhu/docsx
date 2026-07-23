package com.docsx.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 比对任务实体
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@Data
@TableName("compare_task")
public class CompareTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String taskId;
    private String sessionId;
    private String appId;
    private String status;
    private String docType;
    private String fileAName;
    private String fileBName;
    private String fileAPath;
    private String fileBPath;
    private String resultJson;
    private BigDecimal similarity;
    private String errorMsg;
    private String callbackUrl;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
