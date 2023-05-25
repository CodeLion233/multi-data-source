package com.example.datasourcedemo3.controller;

import com.example.datasourcedemo3.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("test5")
    public List<Map<String, Object>> test1() {
        return testService.dmMaps();
    }

    @GetMapping("test6")
    public List<Map<String, Object>> test2() {
        return testService.mysqlMaps();
    }
}
