package com.example.datasourcedemo1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class DatasourceDemo1Application {

    public static void main(String[] args) {
        SpringApplication.run(DatasourceDemo1Application.class, args);
    }

}
