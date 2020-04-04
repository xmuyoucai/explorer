package com.muyoucai.service;

import com.google.common.collect.Lists;
import com.muyoucai.entity.po.RedisServer;
import com.muyoucai.framework.annotation.Autowired;
import com.muyoucai.framework.annotation.Component;
import com.muyoucai.repository.RedisServerRepository;
import com.muyoucai.util.BeanKit;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
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

    public List<RedisServer.Item> list(){
        return getRedisServer().getItems();
    }

    public boolean exists(String name){
        return getItem(name, getRedisServer()) != null;
    }

    public void saveOrUpdate(RedisServer.Item item){
        RedisServer redisServer = getRedisServer();
        if(exists(item.getName())){
            redisServer.getItems().add(item);
        } else {
            RedisServer.Item o = getItem(item.getName(), redisServer);
            BeanKit.copy(item, o);
        }
        repository.save(redisServer);
    }

    public void del(String name){
        if(!exists(name)){
            return;
        }
        RedisServer redisServer = getRedisServer();
        RedisServer.Item item = getItem(name, redisServer);
        redisServer.getItems().remove(item);
        repository.save(redisServer);
    }

    public RedisServer.Item get(String name) {
        for (RedisServer.Item rst : getRedisServer().getItems()) {
            if(rst.getName().equals(name)){
                return rst;
            }
        }
        return null;
    }

    private RedisServer.Item getItem(String name, RedisServer redisServer) {
        for (RedisServer.Item rst : redisServer.getItems()) {
            if(rst.getName().equals(name)){
                return rst;
            }
        }
        return null;
    }

    private RedisServer getRedisServer() {
        RedisServer redisServer = repository.get();
        if (redisServer.getItems() == null) {
            redisServer.setItems(Lists.newArrayList());
        }
        return redisServer;
    }

}
