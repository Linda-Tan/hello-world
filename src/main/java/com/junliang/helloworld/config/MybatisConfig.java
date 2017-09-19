package com.junliang.helloworld.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableTransactionManagement
@MapperScan(basePackages = { "com.junliang.helloworld.dao.mapper" })
public class MybatisConfig  {

    //TODO 2017/9/19 配置多数数据源的事务管理

}
