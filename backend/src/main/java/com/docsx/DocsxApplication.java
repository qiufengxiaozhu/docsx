package com.docsx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

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
        initDirectories();
        SpringApplication.run(DocsxApplication.class, args);
    }

    /**
     * 确保必要的目录存在（SQLite 需要 data 目录预先存在）
     */
    private static void initDirectories() {
        String dataDir = System.getenv("DOCSX_DATA_DIR");
        if (dataDir == null || dataDir.isEmpty()) {
            dataDir = "./data";
        }
        new File(dataDir).mkdirs();
        new File(dataDir + "/files").mkdirs();
        new File(dataDir + "/fonts").mkdirs();
    }
}
