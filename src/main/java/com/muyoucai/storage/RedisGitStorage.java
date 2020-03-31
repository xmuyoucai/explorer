package com.muyoucai.storage;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.muyoucai.annotation.MBean;
import com.muyoucai.annotation.MInjector;
import com.muyoucai.annotation.MValue;
import com.muyoucai.common.IStorage;
import com.muyoucai.model.RedisData;
import com.muyoucai.manager.DB;
import com.muyoucai.util.FileKit;
import com.muyoucai.util.StreamKit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * @author lzy
 */
@Slf4j
@MBean
public class RedisGitStorage implements IStorage<RedisData> {

    @MInjector
    @Getter
    private DB gitManager;

    @MValue("git.localDir")
    private String localDir;

    public RedisGitStorage() {
        gitManager = new DB();
        if(!gitManager.exists(Position.REDIS.getFullPath())){
            gitManager.createDirIfNotExists(Position.REDIS.getFilepath());
            gitManager.createFileIfNotExists(Position.REDIS.getFullPath());
        }
    }

    @Override
    public boolean exists() {
        if(!FileKit.exists(localDir)){

        }

        return false;
    }

    @Override
    public void save(RedisData data) {
        String db = localDir + "/" + Position.REDIS.getFullPath();
        StreamKit.write(JSON.toJSONString(data), db);
        gitManager.upload(Position.REDIS.getFullPath());
    }

    @Override
    public RedisData get() {
        String db = localDir + "/" + Position.REDIS.getFullPath();
        return JSON.toJavaObject(JSON.parseObject(StreamKit.read(db)), RedisData.class);
    }

    public static void main(String[] args) {
        IStorage<RedisData> storage = new RedisGitStorage();
        RedisData data = new RedisData();
        RedisData.Server server = new RedisData.Server();
        server.setHost("120.78.2020.102");
        server.setPort("6379");
        server.setId(UUID.randomUUID().toString());
        data.setServers(Lists.newArrayList(server));
        storage.save(data);
        System.out.println(JSON.toJSONString(storage.get()));
    }

}
