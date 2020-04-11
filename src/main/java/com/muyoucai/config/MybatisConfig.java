package com.muyoucai.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.muyoucai.framework.annotation.LzyAutowired;
import com.muyoucai.framework.annotation.LzyBean;
import com.muyoucai.framework.annotation.LzyConfiguration;
import com.muyoucai.storage.DataSourceFactory;
import com.muyoucai.storage.LzyDataSource;
import com.muyoucai.storage.mapper.RedisHostMapper;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;

@LzyConfiguration
public class MybatisConfig {

    @LzyAutowired
    private LzyDataSource lzyDataSource;

    @LzyBean
    public DataSourceFactory dataSourceFactory() {
        return new DataSourceFactory();
    }

    @LzyBean
    public SqlSessionFactory sqlSessionFactory() {
        DataSource dataSource = dataSourceFactory().getDataSource(lzyDataSource);
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        MybatisConfiguration configuration = new MybatisConfiguration(environment);
        configuration.addMapper(RedisHostMapper.class);
        return new MybatisSqlSessionFactoryBuilder().build(configuration);
    }

}
