package com.muyoucai.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.muyoucai.common.Cfg;
import com.muyoucai.manager.RJedis;
import com.muyoucai.manager.Zoo;
import com.muyoucai.util.CollectionKit;
import com.muyoucai.util.FxUtils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Slf4j
public class ZookeeperController implements Initializable {

    @FXML
    private BorderPane base;

    @FXML
    private TreeTableView<Zoo.Node> table;

    private TreeItem<Zoo.Node> hideRoot;

    private Map<String, TreeItem<Zoo.Node>> roots;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Zookeeper界面控制器初始化 ...");

        roots = Maps.newHashMap();

        try {

            Zoo.Node basic = new Zoo.Node();

            String zooServers = Cfg.PROPERTIES.getProperty("zoo.servers");
            if (!Strings.isNullOrEmpty(zooServers)) {
                for (String address : zooServers.split(",")) {
                    basic.getChildren().add(new Zoo.Node(address));
                }
            }

            hideRoot = transfer(basic);
            log.info("root : {}", JSON.toJSONString(hideRoot));

            table.setRoot(hideRoot);
            table.setShowRoot(false);

            table.setTableMenuButtonVisible(true);

            table.getSelectionModel().selectFirst();

            String[][] tableCfg = new String[][]{{"名称", "name", "300"}, {"路径", "path", "300"}, {"数据", "data", "700"}};
            for (String[] columnCfg : tableCfg) {
                TreeTableColumn<Zoo.Node, String> column = new TreeTableColumn<>(columnCfg[0]);
                column.setCellValueFactory(new TreeItemPropertyValueFactory<>(columnCfg[1]));
                column.setPrefWidth(Integer.parseInt(columnCfg[2]));
                column.setSortable(false);
                column.setCellFactory(col -> {
                    TreeTableCell<Zoo.Node, String> cell = new TreeTableCell(){

                    };
//                    ContextMenu cm = new ContextMenu();
//                    MenuItem miSx = new MenuItem("刷新");
//                    miSx.addEventHandler(EventType.ROOT, e -> {
//                        for (TreeItem<Zoo.Node> root : getSelectedLevel1Items()) {
//                            Zoo zoo = new Zoo(root.getValue().getName());
//                            TreeItem<Zoo.Node> ti = roots.get(root.getValue().getId());
//                            ti.getChildren().clear();
//                            ti.getChildren().add(transfer(zoo.getData()));
//                        }
//                    });
//                    cm.getItems().add(miSx);
//                    cell.setContextMenu(cm);
                    return cell;
                });
                table.getColumns().add(column);
            }
        } catch (Exception e) {
            FxUtils.error(e.getMessage());
        }

    }

    private TreeItem<Zoo.Node> transfer(Zoo.Node node) {
        TreeItem<Zoo.Node> item = new TreeItem<>(node);
        if (node.getLevel() <= 1) {
            item.setExpanded(true);
        }
        if (node.getLevel() == 1) {
            roots.put(node.getId(), item);
        }
        for (Zoo.Node child : node.getChildren()) {
            item.getChildren().add(transfer(child));
        }
        return item;
    }

    private List<TreeItem<Zoo.Node>> getSelectedLevel1Items() {
        return getSelectedItems().stream().filter(ti -> ti.getValue().getLevel() == 1).collect(Collectors.toList());
    }

    private List<TreeItem<Zoo.Node>> getSelectedItems() {
        List<TreeItem<Zoo.Node>> items = Lists.newArrayList();
        items.addAll(table.getSelectionModel().getSelectedItems());
        return items;
    }

    public void delete(ActionEvent event) {
        List<TreeItem<Zoo.Node>> removeList = Lists.newArrayList();
        for (TreeItem<Zoo.Node> root : getSelectedLevel1Items()) {
            for (TreeItem<Zoo.Node> child : hideRoot.getChildren()) {
                if (root.getValue().getId().equals(child.getValue().getId())) {
                    removeList.add(child);
                }
            }
        }
        hideRoot.getChildren().removeAll(removeList);
    }
}
