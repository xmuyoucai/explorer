package com.muyoucai.framework;

import com.muyoucai.FrontEntrance;
import com.muyoucai.repository.RedisServerRepository;
import com.muyoucai.service.SystemService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationContext {

    @Getter
    private static BeanFactory beanFactory;

    @Getter
    public static FrontEntrance entrance;

    public static void main(String[] args) {
        run(args);
    }

    public static void run(String[] args) {
        beanFactory = new BeanFactory("com.muyoucai");
        beanFactory.createAllBeans();
        beanFactory.displayAllBeans();

        getBean(SystemService.class).initialize();
    }

    public static boolean containsBean(Class<?> clz) {
        return beanFactory.containsBean(clz);
    }

    public static <T> T getBean(Class<T> clz) {
        return (T) beanFactory.getBeanByType(clz);
    }
}
