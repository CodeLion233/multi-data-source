package com.example.datasourcedemo1.service.impl;

import com.example.datasourcedemo1.mapper.dm.TestDmMapper;
import com.example.datasourcedemo1.service.Test1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class Test1ServiceImpl implements Test1Service {

    @Autowired
    private TestDmMapper dmMapper;

    @Override
    public List<Map<String, Object>> dmMaps() {
        return dmMapper.selectDm();
    }
}
