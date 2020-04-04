package com.muyoucai.service;

import com.google.common.collect.Lists;
import com.muyoucai.entity.po.RedisServer;
import com.muyoucai.framework.annotation.Autowired;
import com.muyoucai.framework.annotation.Component;
import com.muyoucai.repository.RedisServerRepository;

import java.util.List;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/4 15:58
 * @Version 1.0
 **/
@Component
public class RedisServerService {

    @Autowired
    private RedisServerRepository repository;

    public void init(){
        repository.init();
    }

    public List<RedisServer.Item> list(){
        RedisServer redisServer = repository.get();
        List<RedisServer.Item> items = Lists.newArrayList();
        if(redisServer != null){
            items.addAll(redisServer.getItems());
        }
        return items;
    }

}
