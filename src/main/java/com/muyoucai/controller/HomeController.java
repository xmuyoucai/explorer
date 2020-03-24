package com.muyoucai.controller;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

public class HomeController {

    @FXML
    private MenuItem deleteMenuItem;

    @FXML
    public void click(){
        System.out.println(deleteMenuItem.getText());
    }

}
