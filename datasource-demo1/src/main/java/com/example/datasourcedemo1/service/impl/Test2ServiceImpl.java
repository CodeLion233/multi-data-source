package com.example.datasourcedemo1.service.impl;

import com.example.datasourcedemo1.mapper.mysql.TestMysqlMapper;
import com.example.datasourcedemo1.service.Test2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Service
public class Test2ServiceImpl implements Test2Service {

    @Autowired
    private TestMysqlMapper mysqlMapper;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Override
    public List<Map<String, Object>> mysqlMaps() {
        return mysqlMapper.selectMysql();
    }
}
