package com.muyoucai.view;

import com.muyoucai.view.Epl;
import com.muyoucai.ioc.ApplicationContext;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class EplApp extends Application {

    public static final Epl EPL = new Epl();

    @Override
    public void start(Stage primaryStage) throws IOException {
        EPL.setPrimaryStage(primaryStage);
        EPL.initialize();
    }

    public static void main(String[] args) {
        ApplicationContext.initialize();
        launch(args);
    }
}
