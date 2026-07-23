package com.docsx.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 应用管理实体
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@Data
@TableName("app")
public class App {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String appId;
    private String appName;
    private String appSecret;
    private Integer status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
