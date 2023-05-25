package com.example.datasourcedemo3.service;

import java.util.List;
import java.util.Map;

public interface TestService {

    List<Map<String, Object>> dmMaps();

    List<Map<String, Object>> mysqlMaps();
}
