package com.junliang.spring.config;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
//@EnableTransactionManagement
@MapperScan(basePackages = { "com.junliang.spring.dao.mapper" })
public class MybatisConfig  extends MybatisAutoConfiguration {



    @Value("${spring.datasource.type}")
    private Class<? extends DataSource> dataSourceType;

    public MybatisConfig(MybatisProperties properties, ObjectProvider<Interceptor[]> interceptorsProvider, ResourceLoader resourceLoader, ObjectProvider<DatabaseIdProvider> databaseIdProvider, ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
        super(properties, interceptorsProvider, resourceLoader, databaseIdProvider, configurationCustomizersProvider);
    }
    //TODO 2017/9/20 可配置多数据源

    @Bean(name = "readDataSource")
    @Qualifier("readDataSource")
    @ConfigurationProperties(prefix="spring.datasource.read")
    public DataSource readDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "writeDataSource")
    @Qualifier("writeDataSource")
    @ConfigurationProperties(prefix="spring.datasource.write")
    public DataSource writeDataSource() {
        return DataSourceBuilder.create().build();
    }


    @Bean
    public SqlSessionFactory sqlSessionFactorys() throws Exception {
        return super.sqlSessionFactory(roundRobinDataSouceProxy());
    }

    /**
     * 有多少个数据源就要配置多少个bean
     * @return
     */
    @Bean
    public AbstractRoutingDataSource roundRobinDataSouceProxy() {
        DynamicDataSource proxy = new DynamicDataSource();
        Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
        // 写
        targetDataSources.put("writeDataSource", writeDataSource());
        //读
        targetDataSources.put("readDataSource", readDataSource());

        proxy.setDefaultTargetDataSource(writeDataSource());
        proxy.setTargetDataSources(targetDataSources);
        return proxy;
    }


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
