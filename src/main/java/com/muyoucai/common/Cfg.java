package com.muyoucai.common;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Description
 * @Author lzy
 * @Date 2020/3/29 13:46
 * @Version 1.0
 **/
@Slf4j
public class Cfg {

    public static final int WIDTH = 1355, HEIGHT = 760;
    public static final String TITLE = "One man’s crappy software is another man’s full time job";

    public static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream in = Cfg.class.getClassLoader().getResourceAsStream("config.properties")) {
            PROPERTIES.load(in);
            log.info("Properties :");
            PROPERTIES.forEach((k, v) -> log.info("{} : {}", k, v));
        } catch (IOException e) {
            log.error("加载配置文件失败", e);
        }
    }

}
