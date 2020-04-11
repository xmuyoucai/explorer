package com.muyoucai.framework;

import com.google.common.collect.Sets;
import com.muyoucai.util.ex.CustomException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

@Slf4j
public class ReflectKit {

    public static Set<Field> getAllFields(Class<?> clz){
        Set<Field> fields = Sets.newHashSet();
        if(clz == null){
            return fields;
        }
        fields.addAll(Arrays.asList(clz.getDeclaredFields()));
        fields.addAll(getAllFields(clz.getSuperclass()));
        return fields;
    }

    public static Set<Method> getAllMethods(Class<?> clz){
        Set<Method> methods = Sets.newHashSet();
        if(clz == null){
            return methods;
        }
        methods.addAll(Arrays.asList(clz.getDeclaredMethods()));
        methods.addAll(getAllMethods(clz.getSuperclass()));
        return methods;
    }

    public static Set<String> scan(String baseDir, String scanDir, Set<String> set){
        String fullPath = baseDir + scanDir;
        File file = new File(fullPath);
        if(file.isDirectory()){
            String[] paths = file.list();
            if(paths != null && paths.length >= 0){
                for (int i = 0; i < paths.length; i++) {
                    scan(baseDir, scanDir + "/" + paths[i], set);
                }
            }
        }
        if(file.isFile()){
            if(fullPath.endsWith(".class")){
                set.add(fullPath.replace(baseDir, "").replace(".class", "").replace("/", "."));
            }
        }
        return set;
    }

    public static Set<String> scan(String packageName){
        String scanDir = packageName.replace(".", "/");
        String baseDir = ReflectKit.class.getResource("/").toString().replace("file:/", "");
        return scan(baseDir, scanDir, Sets.newHashSet());
    }

    public static <T> T newInstance(Class<T> clz){
        try {
            log.info("LzyBean instanced : {}", clz.getCanonicalName());
            return clz.newInstance();
        } catch (Exception e) {
            log.error("", e);
            throw new CustomException(e);
        }
    }

    public static Object creationInvoke(Method creation, Object o){
        try {
            log.info("LzyBean created : {} ({}, {})", creation.getReturnType().getCanonicalName(), creation, o);
            return creation.invoke(o);
        } catch (Exception e) {
            log.error("", e);
            throw new CustomException(e);
        }
    }

    public static Class<?> forName(String className){
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new CustomException(e);
        }
    }

    public static <T extends Annotation> boolean has(Class<T> annotation, Class<?> clz){
        return clz.getAnnotation(annotation) != null;
    }

    public static <T extends Annotation> boolean has(Class<T> annotation, Method method){
        return method.getAnnotation(annotation) != null;
    }

    public static <T extends Annotation> boolean has(Class<T> annotation, Field field){
        return field.getAnnotation(annotation) != null;
    }

    public static void main(String[] args) {
        Set<String> set = scan("com.muyoucai.util");
        for (String s : set) {
            System.out.println(s);
        }
    }

}
