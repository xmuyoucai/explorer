package com.muyoucai.framework;

import com.muyoucai.repository.RedisServerRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationContext {

    @Getter
    private static BeanFactory beanFactory;

    public static void main(String[] args) {
        run(args);
    }

    public static void run(String[] args) {
        beanFactory = new BeanFactory("com.muyoucai");
        beanFactory.createAllBeans();
        beanFactory.displayAllBeans();
        RedisServerRepository repository = ApplicationContext.getBean(RedisServerRepository.class);
        repository.init();
    }

    public static boolean containsBean(Class<?> clz) {
        return beanFactory.containsBean(clz);
    }

    public static <T> T getBean(Class<T> clz) {
        return (T) beanFactory.getBeanByType(clz);
    }
}
