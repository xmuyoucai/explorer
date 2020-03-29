package com.muyoucai.util;

import javafx.scene.control.Alert;

/**
 * @Description
 * @Author lzy
 * @Date 2020/3/29 11:11
 * @Version 1.0
 **/
public class FxUtils {

    public static void warn(String content){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(content);
        alert.showAndWait();
    }

}
