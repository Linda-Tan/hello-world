package com.junliang.spring.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Log4j2
@Configuration
public class DataSourceConfig {

    public final static String WRITE_DATASOURCE_KEY ="writeDataSource";
    public final static String READ_DATASOURCE_KEY ="readDataSource";

    @Primary
    @Bean(name = WRITE_DATASOURCE_KEY)
    @Qualifier(WRITE_DATASOURCE_KEY)
    @ConfigurationProperties(prefix = "spring.datasource.write")
    public DataSource writeDataSource() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }

    @Bean(name = READ_DATASOURCE_KEY)
    @Qualifier(READ_DATASOURCE_KEY)
    @ConfigurationProperties(prefix = "spring.datasource.read")
    public DataSource readDataSource() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }


}
