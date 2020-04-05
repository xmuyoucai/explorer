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
            setHgap(5);
            setPrefHeight(20);
            setPadding(new Insets(0));

            Label c1 = new Label(data.getType());
            c1.getStyleClass().add("warm-theme");
            getChildren().add(c1);

            String ttl = data.getTtl() + " ms";
            if(data.getTtl() == -1L){
                ttl = "∞";
            }
            Label c2 = new Label(ttl);
            c2.getStyleClass().add("warm-theme");
            getChildren().add(c2);


            Label c3 = new Label(data.getCount() + " items");
            c3.getStyleClass().add("warm-theme");
            getChildren().add(c3);

        }
    }

}
