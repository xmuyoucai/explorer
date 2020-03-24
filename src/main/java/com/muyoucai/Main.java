package com.muyoucai;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/views/home.fxml"));
        primaryStage.setTitle("One man’s crappy software is another man’s full time job");
        // primaryStage.getIcons().add(new Image(getClass().getResource("/img/icon2.jpg").toString()));
        primaryStage.setScene(new Scene(root, 1355, 760));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
