package com.muyoucai.common;

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
    MAIN("主框架", "/views/main.fxml"),
    HOME("主界面", "/views/home.fxml");

    @Getter
    private String name;
    @Getter
    private String fxml;

}
