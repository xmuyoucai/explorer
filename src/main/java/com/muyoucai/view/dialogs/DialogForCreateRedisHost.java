package com.muyoucai.view.dialogs;


import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.muyoucai.entity.po.RedisHost;
import com.muyoucai.framework.ApplicationContext;
import com.muyoucai.service.RedisHostService;
import com.muyoucai.view.FxUtils;
import com.muyoucai.view.controller.RedisController;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/5 9:48
 * @Version 1.0
 **/
@Slf4j
public class DialogForCreateRedisHost extends Dialog {

    private RedisController controller;

    public DialogForCreateRedisHost(RedisController controller) {
        this.controller = controller;
        initialize();
    }

    private void initialize() {
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

        Dialog<RedisHost> dialog = new Dialog<>();
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
                    return new RedisHost(nameTF.getText(), hostTF.getText(), portTF.getText(), passTF.getText());
                }
            }
            return null;
        });

        Optional<RedisHost> result = dialog.showAndWait();
        if (result.isPresent()) {
            log.info(JSON.toJSONString(result));
            ApplicationContext.getBean(RedisHostService.class).saveOrUpdate(result.get());
            controller.refreshHostsList();
            FxUtils.info("添加成功");
        }
    }
}
