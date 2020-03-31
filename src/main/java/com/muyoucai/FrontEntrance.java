package com.muyoucai;

import com.muyoucai.core.ApplicationContext;
import com.muyoucai.core.Settings;
import com.muyoucai.view.FxUtils;
import com.muyoucai.view.View;
import com.muyoucai.view.ViewManager;
import javafx.application.Application;
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
public class FrontEntrance extends Application {

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
        root = FxUtils.load(View.LAYOUT.getFxml());
        log.info("加载主菜栏 ...");
        root.setTop(FxUtils.load(View.MENU.getFxml()));
        // log.info("加载工具栏 ...");
        // root.setBottom(FxUtils.load(View.TOOLBAR.getFxml()));

        switchTo(View.HOME);

        log.info("初始化场景 ...");
        primaryStage.setScene(new Scene(root, Settings.WIDTH, Settings.HEIGHT));

        primaryStage.setTitle(Settings.TITLE);
        primaryStage.setResizable(false);

        primaryStage.show();
    }

    public void switchTo(View view) throws IOException {
        log.info("切换至 {} ...", view.getName());
        root.setCenter(viewManager.retrieval(view));
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        setPrimaryStage(primaryStage);
        initialize();
    }

    public static void main(String[] args) {
        ApplicationContext.run(args);
        launch(args);
    }

}
