package com.muyoucai.view;

import com.muyoucai.util.ex.CustomException;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
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

    public static void clipboard(String content){
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    public static void info(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(content);
        alert.showAndWait();
    }

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

    public static <T> T load(String fxml) {
        log.info("加载 [{}] ...", fxml);
        try {
            return FXMLLoader.load(FxUtils.class.getResource(fxml));
        } catch (IOException e) {
            log.error("", e);
            throw new CustomException(e);
        }
    }

    public static TextField newTF(String prompt, String text){
        TextField tf = new TextField();
        tf.setText(text);
        tf.setPrefWidth(200);
        tf.setPromptText(prompt);
        return tf;
    }

    public static PasswordField newPF(String prompt, String text){
        PasswordField pf = new PasswordField();
        pf.setText(text);
        pf.setPrefWidth(200);
        pf.setPromptText(prompt);
        return pf;
    }

}
