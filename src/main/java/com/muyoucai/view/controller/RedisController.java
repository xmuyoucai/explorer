package com.muyoucai.view.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.muyoucai.entity.po.RedisServer;
import com.muyoucai.framework.ApplicationContext;
import com.muyoucai.manager.RJedis;
import com.muyoucai.service.RedisServerService;
import com.muyoucai.util.CollectionKit;
import com.muyoucai.view.FxUtils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Slf4j
public class RedisController implements Initializable {

    private final String[][] TABLE_SETTING = new String[][]{{"键", "key", "150"}, {"类型", "type", "150"}, {"TTL", "ttl", "150"}, {"数量", "count", "150"}, {"值", "value", "900"}};

    @FXML
    private TableView<RJedis.RedisItem> tv;
    @FXML
    private TextField patternTF;
    @FXML
    private ComboBox serverBox;

    private RedisServerService rsService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Redis界面控制器初始化 ...");
        this.rsService = ApplicationContext.getBean(RedisServerService.class);
        refreshServerList();
        initializeTV();
    }

    private void refreshServerList() {
        serverBox.getItems().clear();
        List<RedisServer.Item> items = rsService.list();
        if (!CollectionKit.isEmpty(items)) {
            serverBox.getItems().addAll(FXCollections.observableArrayList(items.stream().map(i -> i.getName()).collect(Collectors.toList())));
            serverBox.getSelectionModel().selectFirst();
        }
    }

    /**
     * 查询事件
     *
     * @param event
     */
    public void query(ActionEvent event) {
        String pattern = patternTF.getText().trim();
        if (Strings.isNullOrEmpty(pattern)) {
            FxUtils.error("请输入匹配字符串");
            return;
        }
        if (serverBox.getSelectionModel().getSelectedItem() != null) {
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
        if (serverBox.getSelectionModel().getSelectedItem() != null) {
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
        if (serverBox.getSelectionModel().getSelectedItem() != null) {
            rsService.del(serverBox.getSelectionModel().getSelectedItem().toString());
        }
        refreshServerList();
        FxUtils.info("删除成功");
    }

    /**
     * 添加事件
     *
     * @param event
     */
    public void showAddRedisServerItemDialog(ActionEvent event) {

        FlowPane pane = new FlowPane();
        pane.setPrefWidth(210);
        pane.setVgap(5);
        TextField nameTF = FxUtils.newTF("Name", "");
        pane.getChildren().add(nameTF);
        TextField hostTF = FxUtils.newTF("Host", "");
        pane.getChildren().add(hostTF);
        TextField portTF = FxUtils.newTF("Port", "");
        pane.getChildren().add(portTF);
        PasswordField passTF = FxUtils.newPF("Password", "");
        pane.getChildren().add(passTF);

        Dialog<RedisServer.Item> dialog = new Dialog<>();
        dialog.setTitle("添加");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(pane);


        ButtonType btnOk = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(btnOk);
        dialog.getDialogPane().getButtonTypes().add(btnCancel);

        dialog.getDialogPane().lookupButton(btnOk).addEventFilter(ActionEvent.ACTION,
                filter -> {
                    // Check whether some conditions are fulfilled
                    if (Strings.isNullOrEmpty(nameTF.getText())) {
                        FxUtils.error("Name 不能为空");
                        filter.consume();
                        return;
                    }
                    if (Strings.isNullOrEmpty(hostTF.getText())) {
                        FxUtils.error("Host 不能为空");
                        filter.consume();
                        return;
                    }
                    if (Strings.isNullOrEmpty(portTF.getText())) {
                        FxUtils.error("Port 不能为空");
                        filter.consume();
                        return;
                    }
                });

        dialog.setResultConverter(o -> {
            if (o == btnOk) {
                if (!Strings.isNullOrEmpty(nameTF.getText())
                        && !Strings.isNullOrEmpty(hostTF.getText())
                        && !Strings.isNullOrEmpty(portTF.getText())) {
                    return new RedisServer.Item(nameTF.getText(), hostTF.getText(), portTF.getText(), passTF.getText());
                }
            }
            return null;
        });

        Optional<RedisServer.Item> result = dialog.showAndWait();
        if (result.isPresent()) {
            log.info(JSON.toJSONString(result));
            ApplicationContext.getBean(RedisServerService.class).saveOrUpdate(result.get());
            refreshServerList();
            FxUtils.info("添加成功");
        }

    }

    private void initializeTV() {
        for (int i = 0; i < TABLE_SETTING.length; i++) {
            createTVColumn(i);
        }
        tv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {

            }
        });
    }

    private void createTVColumn(int i) {
        String[] columnCfg = TABLE_SETTING[i];
        TableColumn<RJedis.RedisItem, String> column = new TableColumn<>(columnCfg[0]);
        column.setPrefWidth(Integer.parseInt(columnCfg[2]));
        column.setSortable(false);
        column.setCellValueFactory(new PropertyValueFactory<>(columnCfg[1]));
        final int index = i;
        column.setCellFactory(col -> createTVCell(index));
        tv.getColumns().add(column);
    }

    private TableCell<RJedis.RedisItem, String> createTVCell(int index) {
        TextFieldTableCell<RJedis.RedisItem, String> cell = new TextFieldTableCell<>();
        if (index == 0 || index == 4) {
            createContextMenu(index, cell);
        }
        return cell;
    }

    private void createContextMenu(int index, TextFieldTableCell<RJedis.RedisItem, String> cell) {
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
        RedisServer.Item item = rsService.get(serverBox.getSelectionModel().getSelectedItem().toString());
        return new RJedis(item.getHost(), Integer.parseInt(item.getPort()), item.getPass());
    }
}
