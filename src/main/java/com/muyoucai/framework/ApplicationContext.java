package com.muyoucai.framework;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.muyoucai.config.AppConfig;
import com.muyoucai.framework.annotation.*;
import com.muyoucai.ex.CustomException;
import com.muyoucai.FrontEntrance;
import com.muyoucai.util.ClassUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ApplicationContext {

    public static final FrontEntrance FRONT_ENTRANCE = new FrontEntrance();

    private static final Map<String, BeanDefinition> DEFINITIONS = Maps.newHashMap();
    private static final Map<String, Object> BY_TYPE_BEANS = Maps.newHashMap();

    @Getter
    private static BeanFactory beanFactory;

    public static void main(String[] args) {
        run(args);
    }

    public static void run(String[] args) {
        beanFactory = new BeanFactory("com.muyoucai.config");
        beanFactory.createAllBeans();
        beanFactory.displayAllBeans();
    }

    public static <T> T getBean2(Class<T> clz) {
        return (T) BY_TYPE_BEANS.get(clz.getCanonicalName());
    }

    public static <T> T getBean(Class<T> clz) {
        Object bean = BY_TYPE_BEANS.get(clz.getCanonicalName());
        if (bean != null) {
            log.info("Direct return cached bean : {}", clz.getCanonicalName());
            return (T) bean;
        }
        BeanDefinition def = DEFINITIONS.get(clz.getCanonicalName());
        if (def != null) {
            if (def.getCreation() != null && def.getProxy() != null) {
                return (T) ReflectKit.creationInvoke(def.getCreation(), def.getProxy());
            }
        }
        throw new CustomException(String.format("not found bean definition of %s", clz.getCanonicalName()));
    }

}
