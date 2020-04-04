package com.muyoucai.view.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.muyoucai.framework.ApplicationContext;
import com.muyoucai.framework.Environment;
import com.muyoucai.framework.Settings;
import com.muyoucai.manager.Zoo;
import com.muyoucai.view.FxUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
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

            String zooServers = ApplicationContext.getBean(Environment.class).getString("zoo.servers");
            if (!Strings.isNullOrEmpty(zooServers)) {
                for (String address : zooServers.split(",")) {
                    basic.getChildren().add(new Zoo(address).getData());
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
                column.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Zoo.Node, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Zoo.Node, String> param) {
                        return new SimpleStringProperty(param.getValue().getValue().getPath());
                    }
                });
                column.setPrefWidth(Integer.parseInt(columnCfg[2]));
                column.setSortable(false);
                System.out.println(column.getCellFactory());
                column.setCellFactory(new Callback<TreeTableColumn<Zoo.Node, String>, TreeTableCell<Zoo.Node, String>>() {
                    @Override
                    public TreeTableCell<Zoo.Node, String> call(TreeTableColumn<Zoo.Node, String> param) {
                        TreeTableCell cell = new TextFieldTreeTableCell();
                        return cell;
                    }
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
