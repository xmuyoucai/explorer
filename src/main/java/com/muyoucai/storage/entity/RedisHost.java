package com.muyoucai.storage.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RedisHost {
    private String host, port, pass;

    public String key(){
        return String.format("%s:%s", host, port);
    }
}