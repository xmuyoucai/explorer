package com.muyoucai.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.muyoucai.util.ex.CustomException;
import com.muyoucai.view.FxUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

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

    private String host, pass;
    private int port;

    public RJedis(String host, int port, String pass) {
        this.host = host;
        this.port = port;
        this.pass = pass;
    }

    private Jedis newJedisAndConnectAndAuth() {
        Jedis jedis = new Jedis(host, port);
        if (!Strings.isNullOrEmpty(pass)) {
            jedis.auth(pass);
        }
        return jedis;
    }

    public Set<String> keys(String pattern, Jedis jedis) {
        Set<String> keys = Sets.newHashSet();
        ScanParams sp = new ScanParams();
        sp.count(10);
        sp.match(pattern);
        ScanResult sr;
        String cursor = ScanParams.SCAN_POINTER_START;
        do {
            log.info("scan cursor : {}", (cursor = (sr = jedis.scan(cursor, sp)).getCursor()));
            keys.addAll(sr.getResult());
        }
        while (!sr.isCompleteIteration());
        return keys;
    }

    public Set<String> keys(String pattern) {
        try (Jedis jedis = newJedisAndConnectAndAuth()) {
            return keys(pattern, jedis);
        }
    }

    public RedisItem value(String key, Jedis jedis) {
        if (jedis.exists(key)) {
            String type = jedis.type(key);
            if (RedisDataType.string.name().equals(type)) {
                return new RedisItem(key, type, 1, jedis.get(key), jedis.ttl(key));
            }
            if (RedisDataType.hash.name().equals(type)) {
                return new RedisItem(key, type, jedis.hlen(key), JSON.toJSONString(jedis.hgetAll(key)), jedis.ttl(key));
            }
            if (RedisDataType.list.name().equals(type)) {
                long len = jedis.llen(key);
                return new RedisItem(key, type, len, JSON.toJSONString(jedis.lrange(key, 0, 100)), jedis.ttl(key));
            }
            if (RedisDataType.set.name().equals(type)) {
                long card = jedis.scard(key);
                return new RedisItem(key, type, card, JSON.toJSONString(jedis.smembers(key)), jedis.ttl(key));
            }
            if (RedisDataType.zset.name().equals(type)) {
                long card = jedis.zcard(key);
                return new RedisItem(key, type, card, JSON.toJSONString(jedis.zrange(key, 0, 100)), jedis.ttl(key));
            }
        }
        return null;
    }

    public RedisItem value(String key) {
        try (Jedis jedis = newJedisAndConnectAndAuth()) {
            return value(key, jedis);
        }
    }

    public List<RedisItem> values(Set<String> keys, Jedis jedis) {
        List<RedisItem> items = Lists.newArrayList();
        for (String key : keys) {
            items.add(value(key, jedis));
        }
        return items;
    }

    public List<RedisItem> values(Set<String> keys) {
        try (Jedis jedis = newJedisAndConnectAndAuth()) {
            return values(keys, jedis);
        }
    }

    public List<RedisItem> values(String pattern, Jedis jedis) {
        return values(keys(pattern, jedis));
    }

    public List<RedisItem> values(String pattern) {
        try (Jedis jedis = newJedisAndConnectAndAuth()) {
            return values(keys(pattern, jedis), jedis);
        }
    }

    public String info() {
        try (Jedis jedis = newJedisAndConnectAndAuth()) {
            return jedis.info();
        }
    }

    public List<RedisServerInfoItem> info2() {
        try (Jedis jedis = newJedisAndConnectAndAuth()) {
            List<RedisServerInfoItem> items = Lists.newArrayList();
            String info = jedis.info();
            List<String> sections = Splitter.on("#").omitEmptyStrings().splitToList(info);
            for (String section : sections) {
                List<String> kvList = Lists.newArrayList(Splitter.on("\r\n").omitEmptyStrings().splitToList(section));
                String sec = kvList.remove(0);
                for (String kv : kvList) {
                    String[] kvArr = kv.split(":");
                    if (Strings.isNullOrEmpty(kv)) {
                        continue;
                    }
                    items.add(new RedisServerInfoItem(sec, kvArr[0], kvArr.length != 2 ? "" : kvArr[1]));
                }
            }
            return items;
        }
    }

    public String set(String key, String value) {
        try (Jedis jedis = newJedisAndConnectAndAuth()) {
            return jedis.set(key, value);
        }
    }

    public static void main(String[] args) {
        // System.out.println(JSON.toJSONString(new RJedis("120.78.200.102", 6379, "").info2(), SerializerFeature.PrettyFormat));
        RJedis rJedis = new RJedis("120.78.200.102", 6379, "");
        try (Jedis jedis = rJedis.newJedisAndConnectAndAuth()) {
//            ScanParams sp = new ScanParams();
//            sp.count(1);
//            ScanResult sr;
//            String c = "0";
//            do {
//                sr = jedis.scan(c, sp);
//                System.out.println("cursor : " + (c = sr.getCursor()));
//                for (Object o : sr.getResult()) {
//                    System.out.println(o);
//                }
//            }
//            while (!sr.isCompleteIteration());
        }
    }

    public String info(String section) {
        if (Strings.isNullOrEmpty(section)) {
            return info();
        }
        try (Jedis jedis = newJedisAndConnectAndAuth()) {
            return jedis.info(section);
        }
    }

    @AllArgsConstructor
    public enum RedisDataType {
        string, hash, list, set, zset;
    }

    @AllArgsConstructor
    public enum RedisOperation {
        set("key value [expiration EX seconds|PX milliseconds] [NX|XX]"),
        hset("key field value"),
        lset("key index value"),
        zadd("key [NX|XX] [CH] [INCR] score member [score member ...]");

        @Getter
        private String grammar;

        public static RedisOperation retrieval(String opt) {
            for (RedisOperation operation : values()) {
                if (operation.name().equals(opt)) {
                    return operation;
                }
            }
            throw new CustomException("Operation not supported");
        }
    }

    @Getter
    @AllArgsConstructor
    public static class RedisItem {
        private String key;
        private String type;
        private long count;
        private Object value;
        private long ttl;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class RedisServerInfoItem {
        private String section;
        private String key;
        private String value;
    }

}
