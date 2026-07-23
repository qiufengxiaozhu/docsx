package com.docsx.config;

import org.springframework.context.annotation.Configuration;

/**
 * 数据源配置 - 通过 spring.profiles.active 切换 sqlite / mysql
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@Configuration
public class DataSourceConfig {
    // SQLite 和 MySQL 的切换通过 application-sqlite.yml / application-mysql.yml profiles 实现
}
