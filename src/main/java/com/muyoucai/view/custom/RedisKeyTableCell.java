package com.muyoucai.view.custom;

import com.muyoucai.manager.RJedis;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/5 10:23
 * @Version 1.0
 **/
public class RedisKeyTableCell extends TableCell<RJedis.RedisItem, RJedis.RedisItem> {

    public RedisKeyTableCell() {
    }

    @Override
    protected void updateItem(RJedis.RedisItem item, boolean empty) {
        super.updateItem(item, empty);

        if (!empty) {
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(new RedisKeyItem(item));
        } else {
            setGraphic(null);
        }
    }

    public static class RedisKeyItem extends FlowPane {

        private RJedis.RedisItem data;

        public RedisKeyItem(RJedis.RedisItem data) {

            setVgap(0);
            setHgap(1);
            setPrefHeight(20);
            setPadding(new Insets(0));

            getChildren().add(new Label(data.getKey()));

            Button btn1 = new Button(data.getType());
            // btn1.setStyle("-fx-background-color: linear-gradient(to right,#00fffc,#00fffc);-fx-background-radius: 5;-fx-border-radius: 5;-fx-padding: 0, 2, 0, 2;");
            getChildren().add(btn1);

            Button btn2 = new Button(data.getTtl() + "ms");
            btn2.setStyle("-fx-background-color: linear-gradient(to right,#00fffc,#fff600);-fx-background-radius: 5;-fx-border-radius: 5;-fx-padding: 0, 5, 0, 5;");
            getChildren().add(btn2);


            Label btn3 = new Label(data.getCount() + "items");
            btn3.getStyleClass().add("font-white");
            getChildren().add(btn3);

        }
    }

}
