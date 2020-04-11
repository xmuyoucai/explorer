package com.muyoucai.storage;

import com.alibaba.druid.pool.DruidDataSource;
import javax.sql.DataSource;

public class DataSourceFactory {

    public DataSource getDataSource(LzyDataSource lzyDataSource) {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(lzyDataSource.getUrl());
        datasource.setUsername(lzyDataSource.getUser());
        datasource.setPassword(lzyDataSource.getPass());
        datasource.setDriverClassName(lzyDataSource.getDriver());
        datasource.setInitialSize(1);
        datasource.setMinIdle(1);
        datasource.setMaxActive(20);
        datasource.setMaxWait(60000);
        datasource.setTimeBetweenEvictionRunsMillis(60000);
        datasource.setMinEvictableIdleTimeMillis(300000);
        datasource.setTestWhileIdle(true);
        datasource.setTestOnBorrow(false);
        datasource.setTestOnReturn(false);
        return datasource;
    }

}
