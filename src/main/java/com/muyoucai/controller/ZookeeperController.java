package com.muyoucai.controller;

import com.alibaba.fastjson.JSON;
import com.muyoucai.manager.Zoo;
import com.muyoucai.util.CollectionKit;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class ZookeeperController implements Initializable {

    @FXML
    private BorderPane base;

    @FXML
    private TreeTableView table;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Zookeeper界面控制器初始化 ...");

        try {
            Zoo zoo = new Zoo("120.78.200.102:2181");
            log.info(JSON.toJSONString(zoo.getData()));
            TreeItem<Zoo.Node> root = transfer(zoo.getData());
            log.info("root : {}", JSON.toJSONString(root));
            table.setRoot(root);

            String[][] tableCfg = new String[][]{{"名称", "name", "300"}, {"路径", "path", "300"}, {"数据", "data", "700"}};
            for (String[] columnCfg : tableCfg) {
                TreeTableColumn<Zoo.Node, String> column = new TreeTableColumn<>(columnCfg[0]);
                column.setPrefWidth(Integer.parseInt(columnCfg[2]));
                column.setSortable(false);
                column.setCellValueFactory(new TreeItemPropertyValueFactory<>(columnCfg[1]));
                table.getColumns().add(column);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private TreeItem<Zoo.Node> transfer(Zoo.Node node) {
        TreeItem<Zoo.Node> item = new TreeItem<>(node);
        if(node.getLevel() <= 1){
            item.setExpanded(true);
        }
        for (Zoo.Node child : node.getChildren()) {
            item.getChildren().add(transfer(child));
        }
        return item;
    }

    public void refresh(ActionEvent event) {
        ObservableList<TreeItem<Zoo.Node>> list = table.getSelectionModel().getSelectedItems();
        if (CollectionKit.isEmpty(list)) {
            log.debug("no items selected");
        }
        for (TreeItem<Zoo.Node> treeItem : list) {
            log.debug(treeItem.getValue().getPath());
        }
    }

    public void create(ActionEvent event) {
    }

    public void delete(ActionEvent event) {
        event.getTarget();
    }
}
