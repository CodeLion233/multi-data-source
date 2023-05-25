package com.example.datasourcedemo2.service;

import java.util.List;
import java.util.Map;

public interface TestService {

    List<Map<String, Object>> dmMaps();

    List<Map<String, Object>> mysqlMaps();
}
