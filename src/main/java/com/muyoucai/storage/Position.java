package com.muyoucai.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lzy
 */

@AllArgsConstructor
public enum Position {

    REDIS("redis.myc", "db/"), ZOOKEEPER("zookeeper.myc", "db/");

    @Getter
    private String filename;
    @Getter
    private String filepath;

    public String getFullPath(){
        return filepath + filename;
    }

}
