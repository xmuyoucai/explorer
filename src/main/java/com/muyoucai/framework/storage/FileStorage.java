package com.muyoucai.framework.storage;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.muyoucai.framework.LzyEnvironment;
import com.muyoucai.framework.annotation.LzyAutowired;
import com.muyoucai.framework.annotation.FileSettings;
import com.muyoucai.util.FileKit;
import com.muyoucai.util.StreamKit;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/4 10:52
 * @Version 1.0
 **/
@Slf4j
public abstract class FileStorage<T> implements Storage<T> {

    public abstract Class<T> getEntityClass();

    @LzyAutowired
    private LzyEnvironment env;

    @Override
    public List<T> list() {
        String filepath = String.format("%s/%s", env.getString("epl.base-dir"), getEntityClass().getAnnotation(FileSettings.class).value());
        String json = StreamKit.read(filepath);
        if(!FileKit.exists(filepath)) {
            FileKit.safelyCreateFile(filepath);
            StreamKit.write(JSON.toJSONString(Lists.newArrayList()), filepath);
        }
        log.debug("load data from {} : \n{}", filepath, json);
        return JSON.parseArray(json, getEntityClass());
    }

    @Override
    public void save(List<T> list) {
        String json = JSON.toJSONString(list);
        String filepath = String.format("%s/%s", env.getString("epl.base-dir"), getEntityClass().getAnnotation(FileSettings.class).value());
        if(!FileKit.exists(filepath)) {
            FileKit.safelyCreateFile(filepath);
        }
        log.debug("persist data to {} : {}", filepath, json);
        StreamKit.write(json, filepath);
    }

}
