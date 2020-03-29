package com.muyoucai.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.JFXTreeView;
import com.muyoucai.manager.ZooCtrl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
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
            ZooCtrl.Node root = new ZooCtrl.Node("/","/");
            ZooCtrl.fetchData(root);
            log.info(JSON.toJSONString(root));
            TreeItem<ZooCtrl.Node> treeItem = parse(root);
            log.info("data : {}", JSON.toJSONString(treeItem));
            table.setRoot(treeItem);

            String[][] tableCfg = new String[][]{{"路径", "node"},{"数据", "data"}};
            for (String[] columnCfg : tableCfg) {
                TreeTableColumn<ZooCtrl.Node, String> column = new TreeTableColumn<>(columnCfg[0]);
                column.setCellValueFactory(new TreeItemPropertyValueFactory<>(columnCfg[1]));
                table.getColumns().add(column);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private TreeItem<ZooCtrl.Node> parse(ZooCtrl.Node node){
        TreeItem<ZooCtrl.Node> item = new TreeItem<>(node);
        for (ZooCtrl.Node child : node.getChildren()) {
            item.getChildren().add(parse(child));
        }
        return item;
    }

}
