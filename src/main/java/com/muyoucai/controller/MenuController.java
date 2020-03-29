package com.muyoucai.controller;

import com.muyoucai.EplApp;
import com.muyoucai.common.View;
import com.muyoucai.util.FxUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class MenuController implements Initializable {



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("菜单控制器初始化 ...");
    }

    public void switchTo(ActionEvent event){
        try {
            if(event.getSource() instanceof MenuItem){
                MenuItem mi = (MenuItem) event.getSource();
                String text = mi.getText();
                if("Redis".equals(text)){
                    EplApp.EPL.switchTo(View.REDIS);
                }
                if("Zookeeper".equals(text)){
                    EplApp.EPL.switchTo(View.ZOOKEEPER);
                }
                if("首页".equals(text)){
                    EplApp.EPL.switchTo(View.HOME);
                }
            }

        } catch (IOException e) {
            FxUtils.error(e.getMessage());
        }
    }
}
