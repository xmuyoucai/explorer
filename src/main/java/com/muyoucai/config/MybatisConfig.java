package com.muyoucai.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.muyoucai.framework.annotation.LzyBean;
import com.muyoucai.storage.DataSourceFactory;
import com.muyoucai.storage.mapper.RedisHostMapper;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;

public class MybatisConfig {

    @LzyBean
    public DataSourceFactory dataSourceFactory() {
        return new DataSourceFactory();
    }

    @LzyBean
    public SqlSessionFactory sqlSessionFactory() {
        DataSource dataSource = dataSourceFactory().getDataSource();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        MybatisConfiguration configuration = new MybatisConfiguration(environment);
        configuration.addMapper(RedisHostMapper.class);
        return new MybatisSqlSessionFactoryBuilder().build(configuration);
    }

}
