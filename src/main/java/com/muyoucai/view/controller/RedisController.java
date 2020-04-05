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
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Slf4j
public class RedisController implements Initializable {

    @FXML
    private TableView<RJedis.RedisItem> tvDa;
    @FXML
    private TableView<RJedis.RedisServerInfoItem> tvSi;
    @FXML
    private TextField patternTF;
    @FXML
    private ComboBox<String> hostsBox;
    @FXML
    private ComboBox<String> sectionsBox;

    private RedisHostService rsService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Redis界面控制器初始化 ...");
        this.rsService = ApplicationContext.getBean(RedisHostService.class);
        this.refreshHostsList();
        this.initSectionsList();
        this.refreshRedisServerInfoList();
        this.initializeTVD();
        this.initializeTVS();
    }

    public void refreshHostsList() {
        hostsBox.getItems().clear();
        List<RedisHost> items = rsService.list();
        if (!CollectionKit.isEmpty(items)) {
            hostsBox.getItems().addAll(FXCollections.observableArrayList(items.stream().map(i -> i.getName()).collect(Collectors.toList())));
            hostsBox.getSelectionModel().selectFirst();
        }
    }

    public void initSectionsList() {
        sectionsBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> refreshRedisServerInfoList());
        this.refreshSectionsList();
    }

    public void refreshSectionsList() {
        sectionsBox.getItems().clear();
        RJedis rJedis = createJedis();
        List<RJedis.RedisServerInfoItem> items = rJedis.info2();
        if (!CollectionKit.isEmpty(items)) {
            sectionsBox.getItems().addAll(FXCollections.observableArrayList(items.stream().map(i -> i.getSection()).distinct().collect(Collectors.toList())));
            if (sectionsBox.getSelectionModel().isEmpty()) {
                sectionsBox.getSelectionModel().selectFirst();
            }
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
        refreshRedisDataList(pattern);
    }

    private void refreshRedisDataList(String pattern) {
        if (hostsBox.getSelectionModel().getSelectedItem() != null) {
            RJedis rJedis = createJedis();
            tvDa.setItems(FXCollections.observableArrayList(rJedis.values(pattern)));
        }
    }

    private void refreshRedisServerInfoList() {
        String section = sectionsBox.getSelectionModel().getSelectedItem();
        if (section != null) {
            RJedis rJedis = createJedis();
            List<RJedis.RedisServerInfoItem> items = rJedis.info2();
            tvSi.setItems(FXCollections.observableArrayList(items.stream().filter(i -> i.getSection().equals(section)).collect(Collectors.toList())));
        }
    }

    /**
     * 查看服务器信息事件
     *
     * @param event
     */
    public void showServerInfoDialog(ActionEvent event) {
        if (hostsBox.getSelectionModel().getSelectedItem() != null) {
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
        if (hostsBox.getSelectionModel().getSelectedItem() != null) {
            rsService.del(hostsBox.getSelectionModel().getSelectedItem().toString());
        }
        refreshHostsList();
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

    private void initializeTVD() {
        tvDa.getColumns().add(createTVColumn("KEY", "key", 250));
        tvDa.getColumns().add(createTVColumn("TYPE", "type", 100));
        tvDa.getColumns().add(createTVColumn("TTL", "ttl", 100));
        tvDa.getColumns().add(createTVColumn("ITEMS", "count", 100));
        tvDa.getColumns().add(createTVColumn("VALUE", "value", 700));
    }

    private void initializeTVS() {
        tvSi.getColumns().add(createTVSColumnForSection());
        tvSi.getColumns().add(createTVSColumnForKey());
        tvSi.getColumns().add(createTVSColumnForValue());
    }

    private TableColumn<RJedis.RedisServerInfoItem, String> createTVSColumnForSection() {
        TableColumn<RJedis.RedisServerInfoItem, String> column = new TableColumn<>("Section");
        column.setPrefWidth(150);
        column.setSortable(true);
        column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSection()));
        return column;
    }

    private TableColumn<RJedis.RedisServerInfoItem, String> createTVSColumnForKey() {
        TableColumn<RJedis.RedisServerInfoItem, String> column = new TableColumn<>("Key");
        column.setPrefWidth(250);
        column.setSortable(true);
        column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey()));
        return column;
    }

    private TableColumn<RJedis.RedisServerInfoItem, String> createTVSColumnForValue() {
        TableColumn<RJedis.RedisServerInfoItem, String> column = new TableColumn<>("Value");
        column.setPrefWidth(880);
        column.setSortable(true);
        column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue()));
        return column;
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
            RJedis.RedisItem data = tvDa.getSelectionModel().getSelectedItem();
            // FxUtils.clipboard(data.gi(index).toString());
        });
        MenuItem miCk = new MenuItem("查看");
        miCk.addEventHandler(EventType.ROOT, e -> {
            RJedis.RedisItem data = tvDa.getSelectionModel().getSelectedItem();
            // FxUtils.info(data.gi(index).toString());
        });
        cm.getItems().addAll(miFz, miCk);
        cell.setContextMenu(cm);
    }

    private RJedis createJedis() {
        RedisHost item = rsService.get(hostsBox.getSelectionModel().getSelectedItem().toString());
        return new RJedis(item.getHost(), Integer.parseInt(item.getPort()), item.getPass());
    }

    public void refreshServerInfoList(ActionEvent event) {
        this.refreshRedisServerInfoList();
    }
}
