package com.muyoucai.util;

import com.muyoucai.core.MyMethodInterceptor;
import net.sf.cglib.proxy.Enhancer;

public class CglibKit {

    public static Object createProxy(Class<?> clz){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clz);
        enhancer.setCallback(new MyMethodInterceptor());
        return enhancer.create();
    }

}
