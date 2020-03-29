package com.muyoucai.common;

import com.google.common.collect.Maps;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

/**
 * @Description
 * @Author lzy
 * @Date 2020/3/29 11:55
 * @Version 1.0
 **/
@Slf4j
public class ViewManager {

    @Setter
    private Map<View, BorderPane> views;

    public ViewManager() {
        this.views = Maps.newHashMap();
    }

    public BorderPane retrieval(View view) throws IOException {
        BorderPane c = views.get(view);
        if(c != null){
            return c;
        }
        log.info("加载 [{}] ...", view.getName());
        views.put(view, FXMLLoader.load(getClass().getResource(view.getFxml())));
        return views.get(view);
    }

}
