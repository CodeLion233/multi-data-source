package com.example.datasourcedemo2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class DatasourceDemo2Application {

    public static void main(String[] args) {
        SpringApplication.run(DatasourceDemo2Application.class, args);
    }

}
