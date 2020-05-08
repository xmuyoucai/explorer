package com.muyoucai.service;

import com.muyoucai.entity.po.RedisHost3;
import com.muyoucai.framework.annotation.LzyAutowired;
import com.muyoucai.framework.annotation.LzyComponent;
import com.muyoucai.repository.RedisHostRepository;
import com.muyoucai.util.BeanKit;

import java.util.List;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/4 15:58
 * @Version 1.0
 **/
@LzyComponent
public class RedisHostService {

    @LzyAutowired
    private RedisHostRepository repository;

    public List<RedisHost3> list(){
        return repository.list();
    }

    public RedisHost3 get(String name) {
        for (RedisHost3 host : list()) {
            if(host.getName().equals(name)){
                return host;
            }
        }
        return null;
    }

    public RedisHost3 get(String name, List<RedisHost3> list) {
        for (RedisHost3 host : list) {
            if(host.getName().equals(name)){
                return host;
            }
        }
        return null;
    }

    public boolean exists(String name){
        return get(name) != null;
    }

    public void saveOrUpdate(RedisHost3 host){
        List<RedisHost3> hosts = list();
        if(!exists(host.getName())){
            hosts.add(host);
        } else {
            RedisHost3 entity = get(host.getName(), hosts);
            BeanKit.copy(hosts, entity);
        }
        repository.save(hosts);
    }

    public void del(String name){
        if(!exists(name)){
            return;
        }
        RedisHost3 host = get(name);
        List<RedisHost3> hosts = list();
        hosts.remove(host);
        repository.save(hosts);
    }

}
