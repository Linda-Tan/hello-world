package com.junliang.spring.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

//@Configuration
@EnableJpaRepositories(value = "com.junliang.spring.dao.repository",
        entityManagerFactoryRef = "writeEntityManagerFactory",
        transactionManagerRef="writeTransactionManager")
public class JpaConfig {

    public final static String WRITE_DATASOURCE_KEY ="writeDataSource";
    public final static String READ_DATASOURCE_KEY ="readDataSource";

    @Bean("routingDataSource")
    public AbstractRoutingDataSource routingDataSource(
            @Qualifier(READ_DATASOURCE_KEY) DataSource readDataSource,
            @Qualifier(WRITE_DATASOURCE_KEY) DataSource writeDataSource
    ) throws Exception {
        DynamicDataSource dataSource = new DynamicDataSource();
        Map<Object, Object> targetDataSources = new HashMap(2);
        targetDataSources.put(WRITE_DATASOURCE_KEY, writeDataSource);
        targetDataSources.put(READ_DATASOURCE_KEY, readDataSource);
        dataSource.setTargetDataSources(targetDataSources);
        dataSource.setDefaultTargetDataSource(writeDataSource);
        return dataSource;
    }


    @Autowired
    JpaProperties jpaProperties;

    @Autowired
    @Qualifier("routingDataSource")
    private DataSource routingDataSource;
    /**
     * 我们通过LocalContainerEntityManagerFactoryBean来获取EntityManagerFactory实例
     * @return
     */
    @Bean(name = "writeEntityManagerFactoryBean")
    public LocalContainerEntityManagerFactoryBean writeEntityManagerFactoryBean(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(routingDataSource)
                .properties(jpaProperties.getProperties())
                .packages("com.junliang.spring.pojo.entity") //设置实体类所在位置
                .persistenceUnit("writePersistenceUnit")
                .build();
        //.getObject();//不要在这里直接获取EntityManagerFactory
    }
    /**
     * EntityManagerFactory类似于Hibernate的SessionFactory,mybatis的SqlSessionFactory
     * 总之,在执行操作之前,我们总要获取一个EntityManager,这就类似于Hibernate的Session,
     * mybatis的sqlSession.
     * @param builder
     * @return
     */
    @Bean(name = "writeEntityManagerFactory")
    @Primary
    public EntityManagerFactory writeEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return this.writeEntityManagerFactoryBean(builder).getObject();
    }
    /**
     * 配置事物管理器
     * @return
     */
    @Bean(name = "writeTransactionManager")
    @Primary
    public PlatformTransactionManager writeTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(writeEntityManagerFactory(builder));
    }
}
