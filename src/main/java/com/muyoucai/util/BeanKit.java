package com.muyoucai.util;

import com.google.common.collect.Sets;
import com.muyoucai.ex.CustomException;

import java.lang.annotation.Annotation;
import java.util.Set;

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

}
