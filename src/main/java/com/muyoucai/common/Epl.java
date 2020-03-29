package com.muyoucai.common;

import com.muyoucai.EplApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @Description
 * @Author lzy
 * @Date 2020/3/29 11:22
 * @Version 1.0
 **/
@Slf4j
public class Epl {

    @Getter
    @Setter
    private Stage primaryStage;

    @Getter
    @Setter
    private BorderPane root;

    @Getter
    private final ViewManager viewManager = new ViewManager();

    public void initialize() throws IOException {

        log.info("加载主框架 ...");
        root = FXMLLoader.load(getClass().getResource(View.MAIN.getFxml()));
        log.info("加载主菜栏 ...");
        root.setTop(FXMLLoader.load(getClass().getResource(View.MENU.getFxml())));

        switchTo(View.HOME);

        log.info("初始化场景 ...");
        primaryStage.setScene(new Scene(root, Cfg.WIDTH, Cfg.HEIGHT));

        primaryStage.setTitle(Cfg.TITLE);
        primaryStage.setResizable(false);

        primaryStage.show();
    }

    public void switchTo(View view) throws IOException {
        log.info("加载 {} ...", view.getName());
        root.setCenter(viewManager.retrieval(view));
    }

}
