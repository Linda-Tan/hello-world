package com.junliang.spring.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Configuration
@AutoConfigureAfter({ DataSourceConfig.class })
@Slf4j
@MapperScan(basePackages = { "com.junliang.spring.dao.mapper" })
@EnableTransactionManagement
@ConditionalOnClass({EnableTransactionManagement.class})
public class MybatisConfig  extends MybatisAutoConfiguration {


    @Resource(name = "writeDataSource")
    private DataSource writeDataSource;

    @Resource(name = "readDataSource")
    private DataSource readDataSource;


    public MybatisConfig(MybatisProperties properties, ObjectProvider<Interceptor[]> interceptorsProvider, ResourceLoader resourceLoader, ObjectProvider<DatabaseIdProvider> databaseIdProvider, ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
        super(properties, interceptorsProvider, resourceLoader, databaseIdProvider, configurationCustomizersProvider);
    }


    @Bean
    public SqlSessionFactory sqlSessionFactorys() throws Exception {
        return super.sqlSessionFactory(roundRobinDataSouceProxy());
    }

    /**
     * 有多少个数据源就要配置多少个bean
     * @return
     */
    @Bean(name="routingDataSource")
    public AbstractRoutingDataSource roundRobinDataSouceProxy() {
        DynamicDataSource proxy = new DynamicDataSource();
        Map<Object, Object> targetDataSources = new HashMap<Object, Object>(2);
        // 写
        targetDataSources.put("writeDataSource",writeDataSource);
        //读
        targetDataSources.put("readDataSource", readDataSource);

        proxy.setDefaultTargetDataSource(writeDataSource);
        proxy.setTargetDataSources(targetDataSources);
        return proxy;
    }

    /**
     * 自定义事务
     * MyBatis自动参与到spring事务管理中，无需额外配置，只要org.mybatis.spring.SqlSessionFactoryBean引用的数据源与DataSourceTransactionManager引用的数据源一致即可，否则事务管理会不起作用。
     * @return
     */
    @Bean(name = "transactionManager")
    public DataSourceTransactionManager transactionManagers() {
        log.info("-------------------- transactionManager init ---------------------");
        return new DataSourceTransactionManager(roundRobinDataSouceProxy());
    }


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
