package com.docsx.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Autowired
    private DocsxProperties properties;

    @Bean("docsxTaskExecutor")
    public Executor docsxTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getTask().getThreadPoolSize());
        executor.setMaxPoolSize(properties.getTask().getThreadPoolSize() * 2);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("compare-");
        executor.initialize();
        return executor;
    }
}
