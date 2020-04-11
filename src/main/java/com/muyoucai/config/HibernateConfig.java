package com.muyoucai.config;

import com.muyoucai.framework.annotation.LzyBean;
import com.muyoucai.framework.annotation.LzyConfiguration;
import com.muyoucai.storage.entity.RedisHost;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

@LzyConfiguration
public class HibernateConfig {

    @LzyBean
    public SessionFactory sessionFactory(){
        Configuration configuration = new Configuration();
        configuration.setProperties(properties());
        // configuration.addClass(RedisHost.class);
        return configuration.configure().buildSessionFactory();
    }

    private Properties properties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        properties.setProperty("hibernate.connection.url", "jdbc:h2:~/test");
        properties.setProperty("hibernate.connection.username", "lzy");
        properties.setProperty("hibernate.connection.password", "123456");
        properties.setProperty("hibernate.hbm2ddl.auto", "create");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        return properties;
    }

}
