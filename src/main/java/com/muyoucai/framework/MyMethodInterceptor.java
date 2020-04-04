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
    public Object intercept(Object beanProxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        if(method.getAnnotation(Bean.class) != null) {
            if(ApplicationContext.containsBean(method.getReturnType())){
                log.info("Direct return cached bean : {}", method.getReturnType().getCanonicalName());
                return ApplicationContext.getBean(method.getReturnType());
            }
        }
        return methodProxy.invokeSuper(beanProxy, args);
    }

}
