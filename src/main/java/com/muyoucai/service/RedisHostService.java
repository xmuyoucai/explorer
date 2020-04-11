package com.muyoucai.service;

import com.muyoucai.entity.po.RedisHost;
import com.muyoucai.framework.annotation.Autowired;
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

    @Autowired
    private RedisHostRepository repository;

    public List<RedisHost> list(){
        return repository.list();
    }

    public RedisHost get(String name) {
        for (RedisHost host : list()) {
            if(host.getName().equals(name)){
                return host;
            }
        }
        return null;
    }

    public RedisHost get(String name, List<RedisHost> list) {
        for (RedisHost host : list) {
            if(host.getName().equals(name)){
                return host;
            }
        }
        return null;
    }

    public boolean exists(String name){
        return get(name) != null;
    }

    public void saveOrUpdate(RedisHost host){
        List<RedisHost> hosts = list();
        if(!exists(host.getName())){
            hosts.add(host);
        } else {
            RedisHost entity = get(host.getName(), hosts);
            BeanKit.copy(hosts, entity);
        }
        repository.save(hosts);
    }

    public void del(String name){
        if(!exists(name)){
            return;
        }
        RedisHost host = get(name);
        List<RedisHost> hosts = list();
        hosts.remove(host);
        repository.save(hosts);
    }

}
