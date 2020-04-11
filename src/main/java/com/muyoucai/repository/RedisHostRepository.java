package com.muyoucai.repository;

import com.muyoucai.entity.po.RedisHost;
import com.muyoucai.framework.annotation.LzyComponent;
import com.muyoucai.framework.storage.GitStorage;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/4 12:21
 * @Version 1.0
 **/
@LzyComponent
public class RedisHostRepository extends GitStorage<RedisHost> {
    @Override
    public Class<RedisHost> getEntityClass() {
        return RedisHost.class;
    }
}
