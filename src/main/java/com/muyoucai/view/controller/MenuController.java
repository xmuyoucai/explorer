package com.muyoucai.view.controller;

import com.muyoucai.FrontEntrance;
import com.muyoucai.framework.ApplicationContext;
import com.muyoucai.view.View;
import com.muyoucai.view.FxUtils;
import javafx.event.ActionEvent;
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
        if(event.getSource() instanceof MenuItem){
            MenuItem mi = (MenuItem) event.getSource();
            String text = mi.getText();
            if("Redis".equals(text)){
                FrontEntrance.switchTo(View.REDIS);
            }
            if("Zookeeper".equals(text)){
                FrontEntrance.switchTo(View.ZOOKEEPER);
            }
            if("首页".equals(text)){
                FrontEntrance.switchTo(View.HOME);
            }
            if("Settings".equals(text)){
                FrontEntrance.switchTo(View.SETTINGS);
            }
        }
    }
}
