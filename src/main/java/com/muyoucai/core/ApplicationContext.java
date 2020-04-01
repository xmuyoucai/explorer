package com.muyoucai.core;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.muyoucai.annotation.Bean;
import com.muyoucai.annotation.Configuration;
import com.muyoucai.annotation.Injector;
import com.muyoucai.annotation.Value;
import com.muyoucai.ex.CustomException;
import com.muyoucai.util.CglibKit;
import com.muyoucai.util.ClassKit;
import com.muyoucai.FrontEntrance;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

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

        for (String scanPackage : scanPackages) {
            ClassKit.scan(scanPackage).forEach(s -> instantiate(s));
        }

    }

    private static <T> T getBean(Class<T> clz){
        Object bean = BEAN_BY_TYPE_MAP.get(clz.getCanonicalName());
        if(bean == null){
            throw new CustomException(String.format("not found %s bean", clz.getCanonicalName()));
        }
        return (T) bean;
    }

    private static void instantiate(String className) {
        try {
            Class<?> clz = Class.forName(className);

            Configuration mConfig = clz.getAnnotation(Configuration.class);
            boolean isConfigBean = mConfig != null;

            Bean mBean = clz.getAnnotation(Bean.class);
            boolean isOrdinaryBean = mBean != null;

            if(!isConfigBean && !isOrdinaryBean) return;

            if(BEAN_BY_NAME_MAP.containsKey(className)) return;

            Object bean = CglibKit.createProxy(clz);

            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) {
                Injector mInjector = field.getAnnotation(Injector.class);
                if(mInjector != null){
                    injector(bean, field);
                }
                Value mValue = field.getAnnotation(Value.class);
                if(mValue != null){
                    field.setAccessible(true);
                    Environment environment = (Environment) BEAN_BY_TYPE_MAP.get(Environment.class.getCanonicalName());
                    field.set(bean, environment.get(mValue.value()));
                }
            }

            if(isConfigBean){
                Method[] methods = clz.getDeclaredMethods();
                for (Method method : methods) {
                    Bean mBeanOnMethod = method.getAnnotation(Bean.class);
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

    private static void injector(Object bean, Field field) throws InstantiationException, IllegalAccessException {
        //
        instantiate(field.getType().getCanonicalName());
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
