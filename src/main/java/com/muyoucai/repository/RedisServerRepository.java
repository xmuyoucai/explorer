package com.muyoucai.repository;

import com.muyoucai.entity.po.RedisServer;
import com.muyoucai.framework.annotation.Component;
import com.muyoucai.framework.storage.GitStorage;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/4 12:21
 * @Version 1.0
 **/
@Component
public class RedisServerRepository extends GitStorage<RedisServer> {
    @Override
    public Class<RedisServer> getEntityClass() {
        return RedisServer.class;
    }
}
