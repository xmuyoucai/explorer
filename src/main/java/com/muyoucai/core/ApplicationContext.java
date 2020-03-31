package com.muyoucai.core;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.muyoucai.annotation.MBean;
import com.muyoucai.annotation.MConfig;
import com.muyoucai.annotation.MInjector;
import com.muyoucai.annotation.MValue;
import com.muyoucai.ex.CustomException;
import com.muyoucai.manager.DB;
import com.muyoucai.util.BeanKit;
import com.muyoucai.util.ClassKit;
import com.muyoucai.FrontEntrance;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ApplicationContext {

    public static final FrontEntrance FRONT_ENTRANCE = new FrontEntrance();

    private static final Map<String, Object> BEAN_BY_TYPE_MAP = Maps.newHashMap();
    private static final Map<String, Object> BEAN_BY_NAME_MAP = Maps.newHashMap();

    public static void main(String[] args) {
        run(args);
    }

    public static void run(String[] args){

        BEAN_BY_TYPE_MAP.put(Environment.class.getCanonicalName(), new Environment());

        String[] scanPackages = new String[]{"com.muyoucai.config", "com.muyoucai.manager", "com.muyoucai.storage"};

        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(new MyMethodInterceptor());

        for (String scanPackage : scanPackages) {
            ClassKit.scan(scanPackage).forEach(s -> instantiate(s, enhancer));
        }

    }

    private static <T> T getBean(Class<T> clz){
        Object bean = BEAN_BY_TYPE_MAP.get(clz.getCanonicalName());
        if(bean == null){
            throw new CustomException(String.format("not found %s bean", clz.getCanonicalName()));
        }
        return (T) bean;
    }

    private static void instantiate(String className, Enhancer enhancer) {
        try {
            Class<?> clz = Class.forName(className);

            MConfig mConfig = clz.getAnnotation(MConfig.class);
            boolean isConfigBean = mConfig != null;

            MBean mBean = clz.getAnnotation(MBean.class);
            boolean isOrdinaryBean = mBean != null;

            if(!isConfigBean && !isOrdinaryBean) return;

            if(BEAN_BY_NAME_MAP.containsKey(className)) return;


            enhancer.setSuperclass(clz);

            Object bean = enhancer.create();

            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) {
                MInjector mInjector = field.getAnnotation(MInjector.class);
                if(mInjector != null){
                    injector(bean, field, enhancer);
                }
                MValue mValue = field.getAnnotation(MValue.class);
                if(mValue != null){
                    field.setAccessible(true);
                    Environment environment = (Environment) BEAN_BY_TYPE_MAP.get(Environment.class.getCanonicalName());
                    field.set(bean, environment.get(mValue.value()));
                }
            }

            if(isConfigBean){
                Method[] methods = clz.getDeclaredMethods();
                for (Method method : methods) {
                    MBean mBeanOnMethod = method.getAnnotation(MBean.class);
                    if(mBeanOnMethod == null) continue;
                    log.info("method bean : {}", method.getReturnType().getCanonicalName());
                    Object methodBean = method.invoke(bean);
                    BEAN_BY_TYPE_MAP.put(method.getReturnType().getCanonicalName(), methodBean);
                    BEAN_BY_NAME_MAP.put(method.getName(), methodBean);
                    if(!Strings.isNullOrEmpty(mBeanOnMethod.name())){
                        BEAN_BY_NAME_MAP.put(mBeanOnMethod.name(), methodBean);
                    }
                }
            }

            BEAN_BY_TYPE_MAP.put(className, bean);
            if(isOrdinaryBean && !Strings.isNullOrEmpty(mBean.name())){
                BEAN_BY_NAME_MAP.put(mBean.name(), bean);
            }
        } catch (Exception e) {
            log.error("", e);
            throw new CustomException(e);
        }
    }

    private static void injector(Object bean, Field field, Enhancer enhancer) throws InstantiationException, IllegalAccessException {
        //
        instantiate(field.getType().getCanonicalName(), enhancer);
        //
        Object injectorObj;
        //
        if((injectorObj = BEAN_BY_TYPE_MAP.get(field.getType().getCanonicalName())) == null)
            throw new InstantiationException(String.format("no bean of %s found", field.getType().getCanonicalName()));
        //
        field.setAccessible(true);
        field.set(bean, injectorObj);
    }

}
