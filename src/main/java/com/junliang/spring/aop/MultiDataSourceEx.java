package com.junliang.spring.aop;

import com.junliang.spring.config.DynamicDataSource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Slf4j
@Component
public class MultiDataSourceEx {

    @Pointcut(value = "@annotation(com.junliang.spring.aop.DataSource)")
    private void cut() {
        //定义一个切点
    }

    @Around("cut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        Signature signature = point.getSignature();
        MethodSignature methodSignature = null;
        if (!(signature instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        methodSignature = (MethodSignature) signature;

        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());

        DataSource datasource = currentMethod.getAnnotation(DataSource.class);

        DynamicDataSource.setDataSourceType(datasource.name());
        log.info("设置数据源为：" + datasource.name());

        try {
            return point.proceed();
        } finally {
            log.debug("清空数据源信息！");
            DynamicDataSource.clearDataSourceType();
        }
    }
}
