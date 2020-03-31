package com.muyoucai.ioc;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.muyoucai.annotation.MBean;
import com.muyoucai.annotation.MInjector;
import com.muyoucai.ex.CustomException;
import com.muyoucai.storage.impl.RedisGitStorage;
import com.muyoucai.util.BeanKit;
import com.muyoucai.util.ClassKit;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class MContext {

    private static final Map<String, Object> BEAN_BY_TYPE_MAP = Maps.newHashMap();
    private static final Map<String, Object> BEAN_BY_NAME_MAP = Maps.newHashMap();

    public static void init(){

        Set<String> set = ClassKit.scan("com.muyoucai.storage");
        Set<String> mBeanSet = BeanKit.filter(MBean.class, set);
        mBeanSet.forEach(s -> System.out.println(s));

        for (String className : set) {
            instantiate(className);
        }

    }

    private static void instantiate(String className) {
        try {
            Class<?> clz = Class.forName(className);
            MBean mBean = clz.getAnnotation(MBean.class);
            if(mBean == null) return;
            Object bean = BEAN_BY_NAME_MAP.get(className);

            if(bean != null) return;

            bean = clz.newInstance();

            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) {
                MInjector mInjector = field.getAnnotation(MInjector.class);
                if(mInjector != null){

                    instantiate(field.getType().getCanonicalName());

                    Object injectorObj = BEAN_BY_TYPE_MAP.get(field.getType().getCanonicalName());

                    if(injectorObj == null)
                        throw new InstantiationException(String.format("no bean of %s found", field.getType().getCanonicalName()));

                    if(injectorObj != null){
                        field.setAccessible(true);
                        field.set(bean, injectorObj);
                    }
                }
            }

            BEAN_BY_TYPE_MAP.put(className, bean);
            if(!Strings.isNullOrEmpty(mBean.name())){
                BEAN_BY_NAME_MAP.put(mBean.name(), bean);
            }

        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public static void main(String[] args) {
        init();
    }

}
