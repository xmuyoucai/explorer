package com.muyoucai.storage;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lzy
 */
public interface IStorage {

    void save(RedisServer redisServer);

    class RedisServer {
        @Setter
        @Getter
        private String id, host, port, pass;
    }

}
