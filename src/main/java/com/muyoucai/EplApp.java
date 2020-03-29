package com.muyoucai;

import com.muyoucai.common.Epl;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class EplApp extends Application {

    private final Epl epl = new Epl();

    @Override
    public void start(Stage primaryStage) throws IOException {
        epl.setPrimaryStage(primaryStage);
        epl.initialize();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
