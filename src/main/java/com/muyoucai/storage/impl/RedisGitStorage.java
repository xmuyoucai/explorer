package com.muyoucai.storage.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.muyoucai.storage.IStorage;
import com.muyoucai.storage.Position;
import com.muyoucai.storage.data.RedisData;
import com.muyoucai.manager.RGit;
import com.muyoucai.util.StreamKit;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * @author lzy
 */
@Slf4j
public class RedisGitStorage implements IStorage<RedisData> {

    private RGit rGit;

    public RedisGitStorage() {
        rGit = new RGit();
        if(!rGit.exists(Position.REDIS.getFullPath())){
            rGit.createDirIfNotExists(Position.REDIS.getFilepath());
            rGit.createFileIfNotExists(Position.REDIS.getFullPath());
        }
    }

    @Override
    public void save(RedisData data) {
        String db = rGit.getLocalDir() + "/" + Position.REDIS.getFullPath();
        StreamKit.write(JSON.toJSONString(data), db);
        rGit.upload(Position.REDIS.getFullPath());
    }

    @Override
    public RedisData get() {
        String db = rGit.getLocalDir() + "/" + Position.REDIS.getFullPath();
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
