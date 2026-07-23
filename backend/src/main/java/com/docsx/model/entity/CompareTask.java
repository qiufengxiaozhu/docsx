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
    private String sessionId;
    private String appKey;
    private String status;
    private String docType;
    private String fileName1;
    private String fileName2;
    private String file1Path;
    private String file2Path;
    private String resultJson;
    private BigDecimal similarity;
    private String errorMessage;
    private String callbackUrl;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;
}
