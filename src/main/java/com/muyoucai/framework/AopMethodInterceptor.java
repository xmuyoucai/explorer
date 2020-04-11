package com.muyoucai.framework;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @Description
 * @Author lzy
 * @Date 2020/3/31 23:21
 * @Version 1.0
 **/
@Slf4j
public class AopMethodInterceptor implements MethodInterceptor {

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        log.info("aop ...");
        return methodProxy.invokeSuper(obj, args);
    }

}
