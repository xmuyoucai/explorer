package com.muyoucai.view.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.muyoucai.entity.po.RedisServer;
import com.muyoucai.framework.ApplicationContext;
import com.muyoucai.manager.RJedis;
import com.muyoucai.repository.RedisServerRepository;
import com.muyoucai.service.RedisServerService;
import com.muyoucai.util.CollectionKit;
import com.muyoucai.view.FxUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @FXML
    private ComboBox servers;

    private RedisServerService redisServerService;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Redis界面控制器初始化 ...");
        this.redisServerService = ApplicationContext.getBean(RedisServerService.class);
        refreshServers();
        initTable();
    }

    private void refreshServers() {
        servers.getItems().clear();
        List<RedisServer.Item> items = redisServerService.list();
        if (!CollectionKit.isEmpty(items)) {
            servers.getItems().addAll(FXCollections.observableArrayList(items.stream().map(i -> i.getName()).collect(Collectors.toList())));
            servers.getSelectionModel().selectFirst();
        }
    }

    public void query(ActionEvent event) {
        String pattern = patternTF.getText().trim();
        if (Strings.isNullOrEmpty(pattern)) {
            FxUtils.error("请输入匹配字符串");
            return;
        }
        if(servers.getSelectionModel().getSelectedItem() != null) {
            RJedis rJedis = createJedis();
            tv.setItems(FXCollections.observableArrayList(rJedis.values(pattern)));
        }
    }

    public void info(ActionEvent event) {
        if(servers.getSelectionModel().getSelectedItem() != null) {
            RJedis rJedis = createJedis();
            serverInfoTA.setText(rJedis.info());
        }
    }

    public void add(ActionEvent event) {

        Dialog<RedisServer.Item> dialog = new Dialog<>();
        dialog.setTitle("添加");
        dialog.setResizable(true);

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

        dialog.getDialogPane().setContent(pane);


        ButtonType btnOk = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(btnOk);
        dialog.getDialogPane().getButtonTypes().add(btnCancel);

        final Button btOk = (Button) dialog.getDialogPane().lookupButton(btnOk);
        btOk.addEventFilter(ActionEvent.ACTION, filter -> {
            // Check whether some conditions are fulfilled
            if(Strings.isNullOrEmpty(nameTF.getText())){
                FxUtils.error("Name 不能为空");
                filter.consume();
                return;
            }
            if(Strings.isNullOrEmpty(hostTF.getText())){
                FxUtils.error("Host 不能为空");
                filter.consume();
                return;
            }
            if(Strings.isNullOrEmpty(portTF.getText())){
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
            refreshServers();
            FxUtils.info("添加成功");
        }

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
                if (index == 0 || index == 3) {
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

    public void del(ActionEvent event) {
        if(servers.getSelectionModel().getSelectedItem() != null) {
            redisServerService.del(servers.getSelectionModel().getSelectedItem().toString());
        }
        refreshServers();
        FxUtils.info("删除成功");
    }

    private RJedis createJedis() {
        RedisServer.Item item = redisServerService.get(servers.getSelectionModel().getSelectedItem().toString());
        return new RJedis(item.getHost(), Integer.parseInt(item.getPort()), item.getPass());
    }
}
