package com.muyoucai.framework;

import com.google.common.base.Strings;
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

    private String beanName;

    private Class<?> clz;

    private Object proxy;

    private Method creation;

    public BeanDefinition(Class<?> clz) {
        this.clz = clz;
    }

    public BeanDefinition(String beanName, Class<?> clz) {
        this.beanName = beanName;
        this.clz = clz;
    }

    public BeanDefinition(Object proxy, Method creation) {
        this.proxy = proxy;
        this.creation = creation;
    }

    public BeanDefinition(String beanName, Object proxy, Method creation) {
        this.beanName = beanName;
        this.proxy = proxy;
        this.creation = creation;
    }

    public boolean byType(){
        return Strings.isNullOrEmpty(beanName);
    }

    public boolean instanceByCreation(){
        return creation != null && proxy != null;
    }

}
