package com.muyoucai.framework;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;

@Slf4j
public class CglibKit {

    public static Object createProxy(Class<?> clz){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clz);
        enhancer.setCallback(new MyMethodInterceptor());
        // log.info(clz.getCanonicalName());
        return enhancer.create();
    }

}
