package com.junliang.spring.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableTransactionManagement
@MapperScan(basePackages = { "com.junliang.spring.dao.mapper" })
public class MybatisConfig  {


    //TODO 2017/9/20 可配置druid 多数据源


    //TODO 2017/9/19 配置多数数据源的事务管理

    //TODO 2017/9/20 druid 监控配置
    /**
     * Druid的Servlet
     *
     * @return
     */
    //@Bean
    //public ServletRegistrationBean statViewServletRegistration() {
    //    ServletRegistrationBean registration = new ServletRegistrationBean(new StatViewServlet());
    //
    //    registration.addUrlMappings("/druid/*");
    //    Map map = new HashMap();
    //    // IP白名单 (没有配置或者为空，则允许所有访问)
    //    map.put("allow", "127.0.0.1,192.168.1.126");
    //    // IP黑名单 (存在共同时，deny优先于allow)
    //    map.put("deny", "192.168.1.111");
    //    // 用户名
    //    map.put("loginUsername", "root");
    //    // 密码
    //    map.put("loginPassword", "root");
    //    // 禁用HTML页面上的“Reset All”功能
    //    map.put("resetEnable", "false");
    //    registration.setInitParameters(map);
    //
    //    return registration;
    //}
    //
    ///**
    // * Druid拦截器，用于查看Druid监控
    // *
    // * @return
    // */
    //@Bean
    //public FilterRegistrationBean druidWebStatFilterRegistration() {
    //    FilterRegistrationBean registration = new FilterRegistrationBean(new WebStatFilter());
    //    registration.setName("druidWebStatFilter");
    //    registration.addUrlPatterns("/*");
    //
    //    Map map = new HashMap();
    //    // 忽略资源
    //    map.put("exclusions", "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*");
    //    registration.setInitParameters(map);
    //
    //    return registration;
    //}

}
