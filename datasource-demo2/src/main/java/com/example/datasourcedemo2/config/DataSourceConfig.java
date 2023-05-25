package com.example.datasourcedemo2.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.example.datasourcedemo2.datasource.DynamicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
    public class DataSourceConfig {

    @Bean(name = "db1")
    @ConfigurationProperties(prefix = "spring.datasource.druid.db1")
    public DataSource db1() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "db2")
    @ConfigurationProperties(prefix = "spring.datasource.druid.db2")
    public DataSource db2() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @Primary // Primary 一定要写在这里，写上面任意一个bean相当于指定唯一数据源
    public DynamicDataSource dataSource(
            @Autowired @Qualifier("db1") DataSource firstDataSource,
            @Autowired @Qualifier("db2") DataSource secondDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>(16);
        targetDataSources.put("db1", firstDataSource);
        targetDataSources.put("db2", secondDataSource);
        return new DynamicDataSource(firstDataSource, targetDataSources);
    }
}
