package com.muyoucai.storage.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author lzy
 */
public class RedisData {

    @Getter
    @Setter
    private List<Server> servers;

    public static class Server {
        @Setter
        @Getter
        private String id, host, port, pass;
    }

}
