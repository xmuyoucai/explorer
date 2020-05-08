package com.muyoucai.storage.dao;


import com.muyoucai.storage.entity.RedisHost;

import java.util.List;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/11 20:29
 * @Version 1.0
 **/
public interface IRedisHostAccess {

    void addRedisHost(String host, int port, String pass);

    void removeRedisHost(String key);

    RedisHost getRedisHost(String key);

    List<RedisHost> listRedisHost();

}
