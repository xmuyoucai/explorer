package com.muyoucai.framework;

import com.muyoucai.framework.annotation.Bean;
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
public class MyMethodInterceptor implements MethodInterceptor {

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
//        log.debug("Invoke {}.{}()", obj.getClass().getCanonicalName(), method.getName());
//        if(method.getAnnotation(Bean.class) != null) {
//            Object returnObj = ApplicationContext.getBean2(method.getReturnType());
//            if(returnObj != null){
//                log.info("Bean has created, direct return cached bean : {}", method.getReturnType().getCanonicalName());
//                return returnObj;
//            }
//        }
        Object bean = methodProxy.invokeSuper(obj, args);
//        log.info("Create bean in proxy method invoke : {}", method.getReturnType().getCanonicalName());
//        ApplicationContext.cacheBeanByType(method.getReturnType(), bean);
        return bean;
    }

}
