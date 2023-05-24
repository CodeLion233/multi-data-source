package com.example.datasourcedemo1.mapper.dm;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TestDmMapper {
    @MapKey("id")
    List<Map<String, Object>> selectDm();
}
