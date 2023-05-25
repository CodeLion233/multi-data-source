package com.example.datasourcedemo3.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.datasourcedemo3.mapper.TestMapper;
import com.example.datasourcedemo3.service.TestService;
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
    @DS("db2")
    public List<Map<String, Object>> mysqlMaps() {
        return testMapper.selectMysql();
    }
}
