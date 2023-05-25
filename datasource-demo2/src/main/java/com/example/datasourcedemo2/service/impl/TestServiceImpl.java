package com.example.datasourcedemo2.service.impl;

import com.example.datasourcedemo2.aop.DataSource;
import com.example.datasourcedemo2.mapper.TestMapper;
import com.example.datasourcedemo2.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestMapper testMapper;

    @Override
    public List<Map<String, Object>> dmMaps() {
        return testMapper.selectDm();
    }

    @Override
    @DataSource("db2")
    public List<Map<String, Object>> mysqlMaps() {
        return testMapper.selectMysql();
    }
}
