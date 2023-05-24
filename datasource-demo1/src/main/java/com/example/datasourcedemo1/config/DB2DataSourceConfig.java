package com.example.datasourcedemo1.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.example.datasourcedemo1.mapper.mysql", sqlSessionTemplateRef = "db2SqlSessionTemplate")
public class DB2DataSourceConfig {

    @Bean(name = "db2DataSource")
    @ConfigurationProperties("spring.datasource.db2")
    public DataSource setDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        System.out.println(druidDataSource.getUrl());
        return druidDataSource;
    }

    @Bean(name = "db2TransactionManager")
    public DataSourceTransactionManager setTransactionManager(@Qualifier("db2DataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(new DruidDataSource());
    }

    @Bean(name = "db2SqlSessionFactory")
    public SqlSessionFactory setSqlSessionFactory(@Qualifier("db2DataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/mysql/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "db2SqlSessionTemplate")
    public SqlSessionTemplate setSqlSessionTemplate(@Qualifier("db2SqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
