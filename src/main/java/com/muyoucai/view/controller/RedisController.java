package com.muyoucai.view.controller;

import com.google.common.base.Strings;
import com.muyoucai.entity.po.RedisServer;
import com.muyoucai.framework.ApplicationContext;
import com.muyoucai.framework.Settings;
import com.muyoucai.manager.RJedis;
import com.muyoucai.repository.RedisServerRepository;
import com.muyoucai.util.CollectionKit;
import com.muyoucai.view.FxUtils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class RedisController implements Initializable {

    @FXML
    private TableView<RJedis.RedisItem> tv;

    @FXML
    private Label serverLBL;

    @FXML
    private TextField patternTF;

    @FXML
    private TextArea serverInfoTA;

    private RJedis rJedis;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Redis界面控制器初始化 ...");

        RedisServer redisServer = ApplicationContext.getBean(RedisServerRepository.class).get();
        if(redisServer != null && !CollectionKit.isEmpty(redisServer.getItems())){
//            rJedis = new RJedis(arr[0], Integer.parseInt(arr[1]), arr.length <= 2 ? null : arr[2]);
//            serverInfoTA.setText(rJedis.info());
        }
        initTable();
    }

    private void initTable() {
        String[][] tableCfg = new String[][]{{"键", "key", "300"}, {"类型", "type", "50"}, {"数量", "count", "50"}, {"值", "value", "400"}};
        for (int i = 0; i < tableCfg.length; i++) {
            String[] columnCfg = tableCfg[i];
            TableColumn<RJedis.RedisItem, String> column = new TableColumn<>(columnCfg[0]);
            // column.setPrefWidth(Integer.parseInt(columnCfg[2]));
            column.setSortable(false);
            column.setCellValueFactory(new PropertyValueFactory<>(columnCfg[1]));
            final int index = i;
            column.setCellFactory(col -> {
                TextFieldTableCell<RJedis.RedisItem, String> cell = new TextFieldTableCell<>();
                if(index == 0 || index == 3){
                    ContextMenu cm = new ContextMenu();
                    MenuItem miFz = new MenuItem("复制");
                    miFz.addEventHandler(EventType.ROOT, e -> {
                        RJedis.RedisItem data = tv.getSelectionModel().getSelectedItem();
                        FxUtils.clipboard(data.gi(index).toString());
                    });
                    MenuItem miCk = new MenuItem("查看");
                    miCk.addEventHandler(EventType.ROOT, e -> {
                        RJedis.RedisItem data = tv.getSelectionModel().getSelectedItem();
                        FxUtils.info(data.gi(index).toString());
                    });
                    cm.getItems().addAll(miFz, miCk);
                    cell.setContextMenu(cm);
                }
                return cell;
            });
            tv.getColumns().add(column);
        }
        tv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                serverInfoTA.setText(newValue.getValue().toString());
            }
        });
    }

    public void query(ActionEvent event) {
        String pattern = patternTF.getText().trim();
        if (Strings.isNullOrEmpty(pattern)) {
            FxUtils.error("请输入匹配字符串");
            return;
        }
        tv.setItems(FXCollections.observableArrayList(rJedis.values(pattern)));
    }

    public void info(ActionEvent event) {
        serverInfoTA.setText(rJedis.info());
    }
}
