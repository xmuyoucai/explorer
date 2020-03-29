package com.muyoucai.manager;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.muyoucai.util.CollectionKit;
import com.muyoucai.util.FxUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

/**
 * @Description
 * @Author lzy
 * @Date 2020/3/29 20:49
 * @Version 1.0
 **/
public class RJedis {

    @Getter
    private List<Item> items = Lists.newArrayList();

    public RJedis(String pattern, String address, int port) {
        try (Jedis jedis = new Jedis(address, port)) {
            Set<String> keys = jedis.keys(pattern);
            if (CollectionKit.isEmpty(keys)) {
                return;
            }
            for (String key : keys) {
                try {
                    if(!jedis.exists(key)){
                        continue;
                    }
                    String type = jedis.type(key);
                    if("string".equals(type)){
                        items.add(new Item(key, type, 1, jedis.get(key)));
                    }
                    if("hash".equals(type)){
                        items.add(new Item(key, type, jedis.hlen(key), JSON.toJSONString(jedis.hgetAll(key))));
                    }
                    if("list".equals(type)){
                        long len = jedis.llen(key);
                        items.add(new Item(key, type, len, JSON.toJSONString(jedis.lrange(key, 0, len))));
                    }
                    if("set".equals(type)){
                        long card = jedis.scard(key);
                        items.add(new Item(key, type, card, JSON.toJSONString(jedis.smembers(key))));
                    }
                    if("zset".equals(type)){
                        long card = jedis.zcard(key);
                        items.add(new Item(key, type, card, JSON.toJSONString(jedis.zrange(key, 0, card))));
                    }
                } catch (Exception e) {
                    FxUtils.warn(String.format("获取 %s 失败: %s", key, e.getMessage()));
                }
            }
        } catch (Exception e) {
            FxUtils.error(String.format("获取 redis 数据失败: %s", e.getMessage()));
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Item {
        private String key;
        private String type;
        private long count;
        private Object value;
    }

}
