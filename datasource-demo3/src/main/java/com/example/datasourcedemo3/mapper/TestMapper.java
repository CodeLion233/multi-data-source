package com.example.datasourcedemo3.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TestMapper {

    @MapKey("id")
    List<Map<String, Object>> selectDm();

    @MapKey("id")
    List<Map<String, Object>> selectMysql();
}
