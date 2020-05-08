package com.muyoucai.storage;

import com.alibaba.fastjson.JSON;
import com.muyoucai.storage.entity.Setting2;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

@Slf4j
public class LzyHibernate {

    private LzyDataSource lzyDataSource;
    private Properties properties;

    public LzyHibernate(LzyDataSource lzyDataSource, Properties properties) {
        this.lzyDataSource = lzyDataSource;
        this.properties = properties;
    }

    private Configuration configuration() {
        Configuration configuration = new Configuration();
        configuration.setProperties(properties);
        return configuration;
    }

    public void initialize() {
        try (SessionFactory sessionFactory = configuration().configure().buildSessionFactory()) {
        }
    }

    public void testInsert() {
        try (SessionFactory sessionFactory = configuration().configure().buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            Setting2 entity = new Setting2();
            entity.setKey("test");
            entity.setValue("test");
            session.save(entity);
            log.info("hibernate test insert a setting entry : {}", JSON.toJSONString(entity));
        }
    }

    public void testQuery(){
        try (SessionFactory sessionFactory = configuration().configure().buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            Setting2 entity = session.get(Setting2.class, "test");
            log.info("hibernate test query a setting entry : {}", JSON.toJSONString(entity));
        }
    }

}
