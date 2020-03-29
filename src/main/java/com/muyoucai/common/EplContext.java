package com.muyoucai.common;

import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author lzy
 * @Date 2020/3/29 11:22
 * @Version 1.0
 **/
public class EplContext {

    @Getter
    @Setter
    private Stage primaryStage;

    @Getter
    private final EplSceneManager eplSceneManager = new EplSceneManager();

}
