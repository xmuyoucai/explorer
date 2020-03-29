package com.muyoucai;

import com.muyoucai.common.EplContext;
import com.muyoucai.common.EplSceneManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class EplApp extends Application {

    private final int WIDTH = 1355, HEIGHT = 760;
    private final String VIEW_DIR = "/views";
    private final EplSceneManager.Key START_SCENE = EplSceneManager.Key.home;
    private final String TITLE = "One man’s crappy software is another man’s full time job";

    private EplContext context;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.context = new EplContext();
        context.setPrimaryStage(primaryStage);
        primaryStage.setTitle(TITLE);
        switchScene(START_SCENE);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void switchScene(EplSceneManager.Key key) throws IOException {
        Scene scene = context.getEplSceneManager().getScene(key);
        if(scene == null){
            Parent root = FXMLLoader.load(getClass().getResource(String.format("%s/%s", VIEW_DIR, key.getFxml())));
            scene = new Scene(root, WIDTH, HEIGHT);
            context.getEplSceneManager().saveScene(key, scene);
        }
        context.getPrimaryStage().setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
