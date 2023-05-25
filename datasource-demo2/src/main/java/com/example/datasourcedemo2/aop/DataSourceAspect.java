package com.example.datasourcedemo2.aop;

import com.example.datasourcedemo2.datasource.DynamicDataSource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class DataSourceAspect implements Ordered {

    public static final Logger LOGGER = LoggerFactory.getLogger(DataSourceAspect.class);

    @Pointcut("@annotation(com.example.datasourcedemo2.aop.DataSource)")
    public void dataSourcePointCut() {}

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        DataSource ds = method.getAnnotation(DataSource.class);
        if (ds == null) {
            DynamicDataSource.setDataSource("db1");
            LOGGER.error("set datasource is db1");
        } else {
            DynamicDataSource.setDataSource(ds.value());
            LOGGER.error("set datasource is {}", ds.value());
        }
        try {
            return point.proceed();
        } finally {
            DynamicDataSource.clearDataSource();
        }

    }

    @Override
    public int getOrder() {
        return 1;
    }
}
