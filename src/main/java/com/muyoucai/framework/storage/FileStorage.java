package com.muyoucai.framework.storage;

import com.alibaba.fastjson.JSON;
import com.muyoucai.framework.Environment;
import com.muyoucai.framework.annotation.Autowired;
import com.muyoucai.framework.annotation.FileSettings;
import com.muyoucai.util.StreamKit;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/4 10:52
 * @Version 1.0
 **/
@Slf4j
public abstract class FileStorage<T> implements Storage<T> {

    public abstract Class<T> getEntityClass();

    @Autowired
    private Environment env;

    @Override
    public T get() {
        String filepath = String.format("%s/%s", env.getString("epl.base-dir"), getEntityClass().getAnnotation(FileSettings.class).value());
        String json = StreamKit.read(filepath);
        log.debug("load data from {} : \n{}", filepath, json);
        return JSON.toJavaObject(JSON.parseObject(json), getEntityClass());
    }

    @Override
    public void save(T data) {
        String json = JSON.toJSONString(data);
        String filepath = String.format("%s/%s", env.getString("epl.base-dir"), getEntityClass().getAnnotation(FileSettings.class).value());
        log.debug("persist data to {} : {}", filepath, json);
        StreamKit.write(json, filepath);
    }

}
