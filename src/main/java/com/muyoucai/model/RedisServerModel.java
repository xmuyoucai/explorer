package com.muyoucai.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author lzy
 * @Date 2020/3/31 21:36
 * @Version 1.0
 **/
public class RedisServerModel {

    @Setter
    @Getter
    private String id, host, port, pass;

}
