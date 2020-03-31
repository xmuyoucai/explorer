package com.muyoucai.view;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description
 * @Author lzy
 * @Date 2020/3/29 14:01
 * @Version 1.0
 **/
@AllArgsConstructor
public enum View {
    MENU("菜单栏", "/views/menu.fxml"),
    LAYOUT("主框架", "/views/layout.fxml"),
    HOME("主界面", "/views/home.fxml"),
    REDIS("Redis界面", "/views/redis.fxml"),
    ZOOKEEPER("Zookeeper界面", "/views/zookeeper.fxml"),
    ;

    @Getter
    private String name;
    @Getter
    private String fxml;

}
