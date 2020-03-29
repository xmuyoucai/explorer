package com.muyoucai.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class MenuController implements Initializable {

    @FXML
    private MenuItem deleteMenuItem;

    @FXML
    public void click(){
        System.out.println(deleteMenuItem.getText());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("菜单控制器初始化 ...");
    }

}
