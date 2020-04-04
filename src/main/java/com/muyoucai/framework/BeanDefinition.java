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

    /**
     * Bean Name
     */
    private String name;
    /**
     * Bean Class
     */
    private Class<?> clz;
    /**
     * 创建方法的所属对象
     */
    private Class<?> fromClz;
    /**
     * Bean Method of Creation
     */
    private Method creation;
    /**
     * Bean Instance
     */
    private Object bean;

    private boolean instanced;

    public BeanDefinition(String name, Class<?> clz, Class<?> fromClz, Method creation, Object bean) {
        this.name = name;
        this.clz = clz;
        this.fromClz = fromClz;
        this.creation = creation;
        this.bean = bean;
    }

    public boolean byType(){
        return Strings.isNullOrEmpty(name);
    }

    public boolean instanceByCreation(){
        return creation != null;
    }

}
