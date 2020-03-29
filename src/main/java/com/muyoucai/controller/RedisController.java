package com.muyoucai.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.muyoucai.common.Cfg;
import com.muyoucai.manager.RJedis;
import com.muyoucai.util.FxUtils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
public class RedisController implements Initializable {

    @FXML
    private TableView<RJedis.Item> redisTable;

    @FXML
    private TextField fieldPattern;

    @FXML
    private TextField fieldHost;

    @FXML
    private TextField fieldPort;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Redis界面控制器初始化 ...");

        String redisConf = Cfg.PROPERTIES.getProperty("redis.conf");
        if(Strings.isNullOrEmpty(redisConf)){
            return;
        }
        String[] arr = redisConf.split(":");
        fieldHost.setText(arr[0]);
        fieldPort.setText(arr[1]);

        String[][] tableCfg = new String[][]{{"键", "key", "300"}, {"类型", "type", "50"}, {"数量", "count", "50"}, {"值", "value", "900"}};
        for (String[] columnCfg : tableCfg) {
            TableColumn<RJedis.Item, String> column = new TableColumn<>(columnCfg[0]);
            column.setPrefWidth(Integer.parseInt(columnCfg[2]));
            column.setSortable(false);
            column.setCellValueFactory(new PropertyValueFactory<>(columnCfg[1]));
            redisTable.getColumns().add(column);
        }

    }

    public void query(ActionEvent event) {
        String pattern = fieldPattern.getText().trim();
        if(Strings.isNullOrEmpty(pattern)){
            FxUtils.error("请输入匹配字符串");
            return;
        }
        List<RJedis.Item> items = new RJedis(pattern, fieldHost.getText(), Integer.parseInt(fieldPort.getText())).getItems();
        redisTable.setItems(FXCollections.observableArrayList(items));
    }
}
