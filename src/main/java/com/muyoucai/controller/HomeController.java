package com.muyoucai.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private MenuItem deleteMenuItem;

    @FXML
    public void click(){
        System.out.println(deleteMenuItem.getText());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(String.format("HomeController initialized"));
    }

}
