package com.muyoucai.manager;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.muyoucai.view.FxUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

/**
 * @Description
 * @Author lzy
 * @Date 2020/3/29 20:49
 * @Version 1.0
 **/
@Slf4j
public class RJedis {

    @Getter
    private Jedis jedis;

    /**
     * 创建 Jedis 对象
     *
     * @param address
     * @param port
     * @param pass
     */
    public RJedis(String address, int port, String pass) {
        jedis = new Jedis(address, port);
        if(!Strings.isNullOrEmpty(pass)){
            jedis.auth(pass);
        }
    }

    /**
     * 通过模式匹配获取 key
     *
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern){
        return jedis.keys(pattern);
    }

    /**
     * 通过 keys 获取 values
     *
     * @param keys
     * @return
     */
    public List<RedisItem> values(Set<String> keys){
        List<RedisItem> items = Lists.newArrayList();
        for (String key : keys) {
            items.add(value(key));
        }
        return items;
    }

    /**
     * 通过模式匹配获取 values
     *
     * @param pattern
     * @return
     */
    public List<RedisItem> values(String pattern){
        return values(keys(pattern));
    }

    /**
     * 通过 key 获取 value
     *
     * @param key
     * @return
     */
    public RedisItem value(String key){
        try {
            if(jedis.exists(key)){
                String type = jedis.type(key);
                if("string".equals(type)){
                    return new RedisItem(key, type, 1, jedis.get(key), jedis.ttl(key));
                }
                if("hash".equals(type)){
                    return new RedisItem(key, type, jedis.hlen(key), JSON.toJSONString(jedis.hgetAll(key)), jedis.ttl(key));
                }
                if("list".equals(type)){
                    long len = jedis.llen(key);
                    return new RedisItem(key, type, len, JSON.toJSONString(jedis.lrange(key, 0, 100)), jedis.ttl(key));
                }
                if("set".equals(type)){
                    long card = jedis.scard(key);
                    return new RedisItem(key, type, card, JSON.toJSONString(jedis.smembers(key)), jedis.ttl(key));
                }
                if("zset".equals(type)){
                    long card = jedis.zcard(key);
                    return new RedisItem(key, type, card, JSON.toJSONString(jedis.zrange(key, 0, 100)), jedis.ttl(key));
                }
            }
            return null;
        } catch (Exception e) {
            FxUtils.warn(String.format("获取 %s 失败: %s", key, e.getMessage()));
            return null;
        }
    }

    public String info(){
        return jedis.info();
    }

    /**
     * 关闭连接
     */
    public void close(){
        jedis.close();
    }

    @Getter
    @AllArgsConstructor
    public static class RedisItem {
        private String key;
        private String type;
        private long count;
        private Object value;
        private long ttl;

        public Object gi(int i){
            switch (i){
                case 0:
                    return key;
                case 1:
                    return type;
                case 2:
                    return ttl;
                case 3:
                    return count;
                case 4:
                    return value;
            }
            return null;
        }
    }

}
