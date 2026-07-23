package com.docsx.config;

import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 分页及数据库方言配置
 *
 * @Author Cursor
 * @Date 2026-07-23
 * @Version 1.0
 */
@Configuration
public class SqliteDialect {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInner = new PaginationInnerInterceptor();
        paginationInner.setDbType(DbType.SQLITE);
        paginationInner.setMaxLimit(1000L);
        interceptor.addInnerInterceptor(paginationInner);
        return interceptor;
    }
}
