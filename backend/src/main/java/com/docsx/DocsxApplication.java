package com.docsx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Docsx - 文档比对服务
 * 支持 Word/PDF/Excel 在线比对，三方应用 iframe 集成
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class DocsxApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocsxApplication.class, args);
    }
}
