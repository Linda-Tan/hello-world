package com.junliang.helloworld.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableTransactionManagement
@MapperScan(basePackages = { "com.junliang.helloworld.dao.mapper" })
public class MybatisConfig  {

}
