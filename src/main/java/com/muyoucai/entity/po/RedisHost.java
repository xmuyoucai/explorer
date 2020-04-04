package com.muyoucai.entity.po;

import com.muyoucai.framework.annotation.GitFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/4 12:18
 * @Version 1.0
 **/
@GitFile
@NoArgsConstructor
@AllArgsConstructor
public class RedisHost {

    @Setter
    @Getter
    private String name, host, port, pass;

}


