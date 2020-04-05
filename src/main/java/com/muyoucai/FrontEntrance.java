package com.muyoucai;

import com.muyoucai.framework.ApplicationContext;
import com.muyoucai.framework.Settings;
import com.muyoucai.view.FxUtils;
import com.muyoucai.view.View;
import com.muyoucai.view.ViewManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
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
    private static BorderPane root;

    @Getter
    private static final ViewManager viewManager = new ViewManager();

    @Override
    public void start(Stage primaryStage) throws IOException {
        setPrimaryStage(primaryStage);
        initialize();
    }

    public void initialize() {

        log.info("加载主框架 ...");
        root = FxUtils.load(View.LAYOUT.getFxml());
        log.info("加载主菜栏 ...");
        root.setTop(FxUtils.load(View.MENU.getFxml()));
        // log.info("加载工具栏 ...");
        // root.setBottom(FxUtils.load(View.TOOLBAR.getFxml()));

        switchTo(View.HOME);

        log.info("初始化场景 ...");
        Scene scene = new Scene(root, Settings.WIDTH, Settings.HEIGHT);
        scene.getStylesheets().add("views/global.css");
        primaryStage.setScene(scene);

        primaryStage.setTitle(Settings.TITLE);
        primaryStage.setResizable(false);

        primaryStage.show();
    }

    public static void switchTo(View view) {
        log.info("切换至 {} ...", view.getName());
        root.setCenter(viewManager.retrieval(view));
    }

    public static void main(String[] args) {
        ApplicationContext.run(args);
        launch(args);
    }

}
