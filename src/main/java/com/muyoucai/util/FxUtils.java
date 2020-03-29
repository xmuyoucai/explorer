package com.muyoucai.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @Description
 * @Author lzy
 * @Date 2020/3/29 11:11
 * @Version 1.0
 **/
@Slf4j
public class FxUtils {

    public static void warn(String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(content);
        alert.showAndWait();
    }

    public static void error(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(content);
        alert.showAndWait();
    }

    public static <T> T load(String fxml) throws IOException {
        log.info("加载 [{}] ...", fxml);
        return FXMLLoader.load(FxUtils.class.getResource(fxml));
    }

}
