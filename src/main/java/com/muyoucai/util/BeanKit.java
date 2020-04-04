package com.muyoucai.util;

import com.google.common.collect.Sets;
import com.muyoucai.util.ex.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

@Slf4j
public class BeanKit {

    public static <T extends Annotation> Set<String> filter(Class<T> annotationClz, Set<String> set){
        Set<String> newSet = Sets.newHashSet();
        for (String s : set) {
            Class<?> clz = getClz(s);
            T t = clz.getAnnotation(annotationClz);
            if(t != null) {
                newSet.add(s);
            }
        }
        return newSet;
    }

    public static <A extends Annotation> boolean has(String clzName, Class<A> annClz){
        return getClz(clzName).getAnnotation(annClz) != null;
    }

    public static Class<?> getClz(String clzName){
        try {
            return Class.forName(clzName);
        } catch (ClassNotFoundException e) {
            throw new CustomException(e);
        }
    }

    public static <S, D> void copy(S s, D d){
        try {
            BeanUtils.copyProperties(s, d);
        } catch (Exception e) {
            log.error("", e);
            throw new CustomException(e);
        }
    }

}
