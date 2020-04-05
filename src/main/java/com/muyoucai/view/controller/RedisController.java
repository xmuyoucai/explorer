package com.muyoucai.view.controller;

import com.google.common.base.Strings;
import com.muyoucai.entity.po.RedisHost;
import com.muyoucai.framework.ApplicationContext;
import com.muyoucai.manager.RJedis;
import com.muyoucai.service.RedisHostService;
import com.muyoucai.util.CollectionKit;
import com.muyoucai.view.FxUtils;
import com.muyoucai.view.custom.RedisKeyTableCell;
import com.muyoucai.view.dialogs.DialogForCreateRedisHost;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Slf4j
public class RedisController implements Initializable {

    @FXML
    private TableView<RJedis.RedisItem> tv;
    @FXML
    private TextField patternTF;
    @FXML
    private ComboBox hostBox;

    private RedisHostService rsService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Redis界面控制器初始化 ...");
        this.rsService = ApplicationContext.getBean(RedisHostService.class);
        this.refreshHostList();
        this.initializeTV();
    }

    public void refreshHostList() {
        hostBox.getItems().clear();
        List<RedisHost> items = rsService.list();
        if (!CollectionKit.isEmpty(items)) {
            hostBox.getItems().addAll(FXCollections.observableArrayList(items.stream().map(i -> i.getName()).collect(Collectors.toList())));
            hostBox.getSelectionModel().selectFirst();
        }
    }

    /**
     * 查询事件
     *
     * @param event
     */
    public void queryPattern(ActionEvent event) {
        String pattern = patternTF.getText().trim();
        if (Strings.isNullOrEmpty(pattern)) {
            FxUtils.error("请输入匹配字符串");
            return;
        }
        if (hostBox.getSelectionModel().getSelectedItem() != null) {
            RJedis rJedis = createJedis();
            tv.setItems(FXCollections.observableArrayList(rJedis.values(pattern)));
        }
    }

    /**
     * 查看服务器信息事件
     *
     * @param event
     */
    public void showServerInfoDialog(ActionEvent event) {
        if (hostBox.getSelectionModel().getSelectedItem() != null) {
            RJedis rJedis = createJedis();
            Dialog dialog = new Dialog();
            TextArea ta = new TextArea(rJedis.info());
            ta.setEditable(false);
            ta.setPadding(new Insets(3, 3, 3, 3));
            dialog.getDialogPane().setContent(ta);
            ButtonType btnOk = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(btnOk);
            dialog.show();
        }
    }

    /**
     * 删除服务器事件
     *
     * @param event
     */
    public void deleteRedisServerItem(ActionEvent event) {
        if (hostBox.getSelectionModel().getSelectedItem() != null) {
            rsService.del(hostBox.getSelectionModel().getSelectedItem().toString());
        }
        refreshHostList();
        FxUtils.info("删除成功");
    }

    /**
     * 添加事件
     *
     * @param event
     */
    public void showCreateRedisHostDialog(ActionEvent event) {
        new DialogForCreateRedisHost(this);
    }

    private void initializeTV() {
        tv.getColumns().add(createTVColumnForKey());
        tv.getColumns().add(createTVColumn("Value", "value", 900));
    }

    private TableColumn<RJedis.RedisItem, RJedis.RedisItem> createTVColumnForKey() {
        TableColumn<RJedis.RedisItem, RJedis.RedisItem> column = new TableColumn<>("Key");
        column.setPrefWidth(250);
        column.setSortable(true);
        column.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
        column.setCellFactory(col -> new RedisKeyTableCell());
        return column;
    }

    private void createContextMenuForKey(TextFieldTableCell<RJedis.RedisItem, String> cell) {
        ContextMenu cm = new ContextMenu();
        MenuItem miFz = new MenuItem("Copy Key");
        miFz.addEventHandler(EventType.ROOT, e -> {
            RJedis.RedisItem data = tv.getSelectionModel().getSelectedItem();
            FxUtils.clipboard(data.getKey());
        });
        MenuItem miCk = new MenuItem("Show Detail");
        miCk.addEventHandler(EventType.ROOT, e -> {
            RJedis.RedisItem data = tv.getSelectionModel().getSelectedItem();
            FxUtils.info(data.getKey());
        });
        cm.getItems().addAll(miFz, miCk);
        cell.setContextMenu(cm);
    }

    private TableColumn<RJedis.RedisItem, String> createTVColumn(String head, String field, int width) {
        TableColumn<RJedis.RedisItem, String> column = new TableColumn<>(head);
        column.setPrefWidth(width);
        column.setSortable(false);
        column.setCellValueFactory(new PropertyValueFactory<>(field));
        column.setCellFactory(col -> createTVCell());
        return column;
    }

    private TableCell<RJedis.RedisItem, String> createTVCell() {
        TextFieldTableCell<RJedis.RedisItem, String> cell = new TextFieldTableCell<>();
        return cell;
    }

    private void createContextMenuForKey(int index, TextFieldTableCell<RJedis.RedisItem, String> cell) {
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

    private RJedis createJedis() {
        RedisHost item = rsService.get(hostBox.getSelectionModel().getSelectedItem().toString());
        return new RJedis(item.getHost(), Integer.parseInt(item.getPort()), item.getPass());
    }
}
