package com.muyoucai.storage;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.muyoucai.storage.dao.IDictionaryAccess;
import com.muyoucai.storage.dao.IRedisHostAccess;
import com.muyoucai.storage.entity.Dictionary;
import com.muyoucai.storage.entity.RedisHost;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/11 19:59
 * @Version 1.0
 **/
public class LzyStorageImpl implements ILzyStorage, IRedisHostAccess, IDictionaryAccess {

    private DB db;

    private String location;

    public LzyStorageImpl(String location) throws IOException {
        this.location = location;
        this.db = load();
    }

    private DB load() throws IOException {
        File dbFile = new File(this.location);
        if(!dbFile.exists()){
            Files.createParentDirs(dbFile);
            dbFile.createNewFile();
            Files.write("{}".getBytes(), dbFile);
        }
        String json = Joiner.on("").join(Files.readLines(dbFile, Charset.forName("UTF-8")));
        return JSON.parseObject(json, DB.class);
    }

    private void persist() {

    }

    @Override
    public void addRedisHost(String host, int port, String pass) {
        String key = String.format("%s:%s", host, port);
        db.memRedisHost.put(key, RedisHost.builder().host(host).port(String.valueOf(port)).pass(pass).build());
    }

    @Override
    public void removeRedisHost(String key) {
        if(db.memRedisHost.containsKey(key)){
            db.memRedisHost.remove(key);
        }
    }

    @Override
    public RedisHost getRedisHost(String key) {
        if(db.memRedisHost.containsKey(key)){
            return db.memRedisHost.get(key);
        }
        return null;
    }

    @Override
    public List<RedisHost> listRedisHost() {
        return db.memRedisHost.values().stream().collect(Collectors.toList());
    }

    @Override
    public IRedisHostAccess getRedisHostStorage() {
        return this;
    }

    @Override
    public IDictionaryAccess getDictionaryStorage() {
        return this;
    }

    @Override
    public void addDictionary(String key, String value) {
        db.memDictionary.put(key, Dictionary.builder().key(key).value(value).build());
    }

    @Override
    public void removeDictionary(String key) {
        db.memDictionary.remove(key);
    }

    @Override
    public Dictionary getDictionary(String key) {
        return db.memDictionary.get(key);
    }

    @Override
    public List<Dictionary> listDictionary() {
        return db.memDictionary.values().stream().collect(Collectors.toList());
    }

    static class DB {
        private Map<String, RedisHost> memRedisHost = Maps.newHashMap();
        private Map<String, Dictionary> memDictionary = Maps.newHashMap();
    }

}
