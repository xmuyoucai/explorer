package com.muyoucai.entity.po;

import com.muyoucai.framework.annotation.GitFile;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/4 12:18
 * @Version 1.0
 **/
@GitFile
public class RedisServer {

    @Getter
    @Setter
    private List<Item> items;

    public static class Item {
        @Setter
        @Getter
        private String id, host, port, pass;
    }

}
