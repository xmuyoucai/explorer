package com.muyoucai.framework;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/1 21:11
 * @Version 1.0
 **/
@Getter
@Setter
public class BeanDefinition {

    private Class<?> clz;

    private Object proxy;

    private Method creation;

    public BeanDefinition(Class<?> clz) {
        this.clz = clz;
    }

    public BeanDefinition(Object proxy, Method creation) {
        this.proxy = proxy;
        this.creation = creation;
    }
}
