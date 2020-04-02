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

    public static boolean containsBean(Class<?> clz) {
        return beanFactory.containsBean(clz);
    }

    public static <T> T getBean(Class<T> clz) {
        return (T) beanFactory.getBeanByType(clz);
    }
}
