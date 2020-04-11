package com.muyoucai.framework;

import com.google.common.collect.Maps;
import com.muyoucai.framework.annotation.LzyComponent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * @Description
 * @Author lzy
 * @Date 2020/3/31 21:46
 * @Version 1.0
 **/
@Slf4j
@Getter
@LzyComponent
public class LzyEnvironment {

    private Map<Object, Object> cfg;

    public LzyEnvironment() {
        Properties properties = new Properties();
        try (InputStream in = Settings.class.getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(in);
            cfg = Maps.newHashMap();
            properties.forEach((k, v) -> cfg.put(k, v));
        } catch (IOException e) {
            log.error("load 'config.properties' failed", e);
        }
    }

    public <T> T get(String key){
        return (T) cfg.get(key);
    }

    public String getString(String key){
        return (String) cfg.get(key);
    }

}
