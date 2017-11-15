package com.junliang.spring.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

@Configuration
@ConditionalOnClass({MybatisConfig.class})
@EnableJpaRepositories(value = "com.junliang.spring.dao.repository",
        entityManagerFactoryRef = "writeEntityManagerFactoryBean",
        transactionManagerRef="writeTransactionManager")
public class JpaConfig {


    @Autowired
    JpaProperties jpaProperties;

    @Autowired
    @Qualifier("routingDataSource")
    private DataSource routingDataSource;


    @Primary
    @Bean(name = "entityManagerPrimary")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return writeEntityManagerFactoryBean(builder).getObject().createEntityManager();
    }

    /**
     * 我们通过LocalContainerEntityManagerFactoryBean来获取EntityManagerFactory实例
     * @return
     */
    @Bean(name = "writeEntityManagerFactoryBean")
    public LocalContainerEntityManagerFactoryBean writeEntityManagerFactoryBean (EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(routingDataSource)
                .properties(jpaProperties.getProperties())
                .packages("com.junliang.spring.pojo.entity") //设置实体类所在位置
                .persistenceUnit("writePersistenceUnit")
                .build();
        //.getObject();//不要在这里直接获取EntityManagerFactory
    }


    /**
     * 配置事物管理器
     * @return
     */
    @Bean(name = "writeTransactionManager")
    @Primary
    public PlatformTransactionManager writeTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(writeEntityManagerFactoryBean(builder).getObject());
    }
}
