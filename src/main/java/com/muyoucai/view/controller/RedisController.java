package com.muyoucai.view.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.muyoucai.entity.po.RedisHost;
import com.muyoucai.framework.ApplicationContext;
import com.muyoucai.manager.RJedis;
import com.muyoucai.service.RedisHostService;
import com.muyoucai.storage.mapper.RedisHostMapper;
import com.muyoucai.util.CollectionKit;
import com.muyoucai.util.DateUtils;
import com.muyoucai.view.FxUtils;
import com.muyoucai.view.dialogs.DialogForCreateRedisHost;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Slf4j
public class RedisController implements Initializable {

    @FXML
    public ComboBox<String> operationsBox;
    @FXML
    public Label lblGrammar;
    @FXML
    public TextArea tfParams;
    @FXML
    public TextArea logTA;
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
        log.info("Redis界面控制器初始化 ...");
        this.rsService = ApplicationContext.getBean(RedisHostService.class);
        this.refreshHostsList();
        this.initSectionsList();
        this.initOperationsList();
        this.refreshRedisServerInfoList();
        this.initializeTVD();
        this.initializeTVS();
//        try (SqlSession sqlSession = ApplicationContext.getBean(SqlSessionFactory.class).openSession()) {
//            RedisHostMapper mapper = sqlSession.getMapper(RedisHostMapper.class);
//            System.out.println("------------------ : " + mapper.select1());
//            System.out.println("------------------ : " + mapper.selectList(new QueryWrapper<>()));
//        }



        try (
                SessionFactory sessionFactory = ApplicationContext.getBean(SessionFactory.class);
                Session session = sessionFactory.openSession();
        ) {
            com.muyoucai.storage.entity.RedisHost rh = new com.muyoucai.storage.entity.RedisHost();
            rh.setHost("111");
            session.save(rh);

            com.muyoucai.storage.entity.RedisHost e = session.get(com.muyoucai.storage.entity.RedisHost.class, 1);
            System.out.println("hibernate1 : " + JSON.toJSONString(e));
        }
        SqlSessionFactory sqlSessionFactory = ApplicationContext.getBean(SqlSessionFactory.class);
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            RedisHostMapper mapper = sqlSession.getMapper(RedisHostMapper.class);
            com.muyoucai.storage.entity.RedisHost entity = mapper.selectById(1);
            System.out.println("mybatis : " + JSON.toJSONString(entity));
        }
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

    public void initOperationsList() {
        List<String> operations = Arrays.asList(RJedis.RedisOperation.values()).stream().map(i -> i.name()).collect(Collectors.toList());
        operationsBox.getItems().addAll(FXCollections.observableArrayList(operations));
        operationsBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> refreshGrammarLabel());
        operationsBox.getSelectionModel().selectFirst();
    }

    public void initSectionsList() {
        sectionsBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> refreshRedisServerInfoList());
        this.refreshSectionsList();
    }

    public void refreshHostsList() {
        hostsBox.getItems().clear();
        List<RedisHost> items = rsService.list();
        if (!CollectionKit.isEmpty(items)) {
            hostsBox.getItems().addAll(FXCollections.observableArrayList(items.stream().map(i -> i.getName()).collect(Collectors.toList())));
            hostsBox.getSelectionModel().selectFirst();
        }
    }

    public void refreshSectionsList() {
        sectionsBox.getItems().clear();
        RJedis rJedis = createJedis();
        if (rJedis != null) {
            List<RJedis.RedisServerInfoItem> items = rJedis.info2();
            if (!CollectionKit.isEmpty(items)) {
                sectionsBox.getItems().addAll(FXCollections.observableArrayList(items.stream().map(i -> i.getSection()).distinct().collect(Collectors.toList())));
                if (sectionsBox.getSelectionModel().isEmpty()) {
                    sectionsBox.getSelectionModel().selectFirst();
                }
            }
        }
    }

    private void refreshRedisDataList(String pattern) {
        if (hostsBox.getSelectionModel().getSelectedItem() != null) {
            RJedis rJedis = createJedis();
            if (rJedis != null) {
                tvDa.setItems(FXCollections.observableArrayList(rJedis.values(pattern)));
            }
        }
    }

    private void refreshRedisServerInfoList() {
        String section = sectionsBox.getSelectionModel().getSelectedItem();
        if (section != null) {
            RJedis rJedis = createJedis();
            if (rJedis != null) {
                List<RJedis.RedisServerInfoItem> items = rJedis.info2();
                tvSi.setItems(FXCollections.observableArrayList(items.stream().filter(i -> i.getSection().equals(section)).collect(Collectors.toList())));
            }
        }
    }

    private void refreshGrammarLabel() {
        String opt = operationsBox.getSelectionModel().getSelectedItem();
        if (opt != null) {
            lblGrammar.setText(RJedis.RedisOperation.retrieval(opt).getGrammar());
        }
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
        TableColumn<RJedis.RedisServerInfoItem, String> column = new TableColumn<>("LzyValue");
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
        if (!hostsBox.getSelectionModel().isEmpty()) {
            RedisHost item = rsService.get(hostsBox.getSelectionModel().getSelectedItem().toString());
            return new RJedis(item.getHost(), Integer.parseInt(item.getPort()), item.getPass());
        }
        return null;
    }

    private void log(String msg) {
        logTA.appendText(String.format("[%s] %s", DateUtils.formatCurrentDate(), msg));
    }

    /**
     * 查询事件
     *
     * @param event
     */
    public void actionQueryPattern(ActionEvent event) {
        String pattern = patternTF.getText().trim();
        if (Strings.isNullOrEmpty(pattern)) {
            FxUtils.error("请输入匹配字符串");
            return;
        }
        refreshRedisDataList(pattern);
    }

    /**
     * 删除服务器事件
     *
     * @param event
     */
    public void actionDeleteRedisServerItem(ActionEvent event) {
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
    public void actionShowCreateRedisHostDialog(ActionEvent event) {
        new DialogForCreateRedisHost(this);
    }

    public void actionRefreshServerInfoList(ActionEvent event) {
        this.refreshRedisServerInfoList();
    }

    public void actionExecute(ActionEvent event) {
        RJedis rJedis = createJedis();
        if (rJedis == null) {
            return;
        }
        String params = tfParams.getText().trim();
        if (Strings.isNullOrEmpty(params)) {
            log("请求输入执行参数\n");
            return;
        }

        String opt = operationsBox.getSelectionModel().getSelectedItem();
        if (RJedis.RedisOperation.set.name().equals(opt)) {
            String[] paramArr = params.split("\\s+");
            if (paramArr.length < 2 || paramArr.length > 5) {
                log(String.format("参数错误：%s\n", params));
                return;
            }
            if (paramArr.length == 2) {

            }
            log(String.format("执行语句：[ set %s %s ]，执行结果：%s\n", paramArr[0], paramArr[1], rJedis.set(paramArr[0], paramArr[1])));
            return;
        }

    }

    public void actionClearLog(ActionEvent event) {
        logTA.setText("");
    }
}
