package com.junliang.spring.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Created by Toni_ on 2017/7/16.
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    //目标数据源
    private static final ThreadLocal<String> STRING_THREAD_LOCAL = new ThreadLocal<>();

    //public static final String READ ="readDataSource";
    //public static final String WRITE ="writeDataSource";


    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSourceType();
    }
    /**
     * 设置数据源类型
     *
     * @param dataSourceType 数据库类型
     */
    public static void setDataSourceType(String dataSourceType) {
        STRING_THREAD_LOCAL.set(dataSourceType);
    }

    /**
     * 获取数据源类型
     */
    public static String getDataSourceType() {
        //System.out.println(STRING_THREAD_LOCAL.get());
        //if (STRING_THREAD_LOCAL.get()==null)
        //    return   DataSourceConfig.WRITE_DATASOURCE_KEY;
        return STRING_THREAD_LOCAL.get();
    }

    /**
     * 清除数据源类型
     */
    public static void clearDataSourceType() {
        STRING_THREAD_LOCAL.remove();
    }
}
