package com.muyoucai.storage.data;

import com.google.common.base.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RedisData)) return false;
        RedisData redisData = (RedisData) o;
        return Objects.equal(servers, redisData.servers);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(servers);
    }

    public static class Server {
        @Setter
        @Getter
        private String id, host, port, pass;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Server server = (Server) o;
            return Objects.equal(id, server.id) &&
                    Objects.equal(host, server.host) &&
                    Objects.equal(port, server.port) &&
                    Objects.equal(pass, server.pass);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id, host, port, pass);
        }
    }

}
