package com.muyoucai.storage.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/11 20:47
 * @Version 1.0
 **/
@Getter
@Setter
@Builder
public class Dictionary {
    private String key, value;
}
