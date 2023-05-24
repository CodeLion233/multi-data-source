package com.example.datasourcedemo1.controller;

import com.example.datasourcedemo1.service.Test1Service;
import com.example.datasourcedemo1.service.Test2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@EnableTransactionManagement
public class TestController {

    @Autowired
    private Test1Service test1Service;
    @Autowired
    private Test2Service test2Service;

    @GetMapping("test1")
    public List<Map<String, Object>> dmMap() {
        return test1Service.dmMaps();
    }

    @GetMapping("test2")
    public List<Map<String, Object>> mysqlMap() {
        return test2Service.mysqlMaps();
    }
}
