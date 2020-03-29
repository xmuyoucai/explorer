package com.muyoucai.common;

import com.google.common.collect.Maps;
import javafx.scene.Scene;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @Description
 * @Author lzy
 * @Date 2020/3/29 11:55
 * @Version 1.0
 **/
public class EplSceneManager {

    @Setter
    private Map<Key, Scene> scenes;

    public EplSceneManager() {
        this.scenes = Maps.newHashMap();
    }

    public Scene getScene(Key key){
        return scenes.get(key);
    }

    public void saveScene(Key key, Scene scene){
        scenes.put(key, scene);
    }

    @AllArgsConstructor
    public enum Key {
        home("home.fxml");

        @Getter
        private String fxml;
    }

}
