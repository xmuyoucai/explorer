package com.muyoucai.config;

import com.muyoucai.framework.annotation.LzyAutowired;
import com.muyoucai.framework.annotation.LzyBean;
import com.muyoucai.framework.annotation.LzyConfiguration;
import com.muyoucai.storage.LzyDataSource;
import com.muyoucai.storage.LzyHibernate;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

@LzyConfiguration
public class HibernateConfig {

    @LzyAutowired
    private LzyDataSource lzyDataSource;

    @LzyBean
    public LzyHibernate lzyHibernate(){
        LzyHibernate lzyHibernate;
        (lzyHibernate = new LzyHibernate(lzyDataSource, properties())).initialize();
        return lzyHibernate;
    }

    private Properties properties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.driver_class", lzyDataSource.getDriver());
        properties.setProperty("hibernate.connection.url", lzyDataSource.getUrl());
        properties.setProperty("hibernate.connection.username", lzyDataSource.getUser());
        properties.setProperty("hibernate.connection.password", lzyDataSource.getPass());
        properties.setProperty("hibernate.hbm2ddl.auto", "create");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");
        return properties;
    }

}
