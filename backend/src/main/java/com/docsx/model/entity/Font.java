package com.docsx.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字体管理实体
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@Data
@TableName("font")
public class Font {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String fontName;
    private String fontFamily;
    private String filePath;
    private Long fileSize;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
