package com.muyoucai.framework;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.muyoucai.ex.*;
import com.muyoucai.framework.annotation.Autowire;
import com.muyoucai.framework.annotation.Bean;
import com.muyoucai.framework.annotation.Component;
import com.muyoucai.framework.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/2 21:05
 * @Version 1.0
 **/
@Slf4j
public class BeanFactory {

    private Set<Class<?>> all = Sets.newHashSet();
    private Set<Class<?>> configurationSet = Sets.newHashSet();
    private Set<Class<?>> componentSet = Sets.newHashSet();
    private Map<String, BeanDefinition> byNameDefinitions = Maps.newHashMap();
    private Map<Class<?>, BeanDefinition> byTypeDefinitions = Maps.newHashMap();

    private Map<String, Object> byNameBeans = Maps.newHashMap();
    private Map<Class<?>, Object> byTypeBeans = Maps.newHashMap();


    public BeanFactory(String ... args) {
        discover(args);
        classify();
        createBeanDefinition();
    }

    public Object getBeanByName(String name){
        return byNameBeans.get(name);
    }

    public Object getBeanByType(Class<?> clz){
        return byTypeBeans.get(clz);
    }

    public void createAllBeans() {
        byNameDefinitions.forEach((beanName, def) -> instance(def));
        byTypeDefinitions.forEach((clz, def) -> instance(def));
    }

    public void displayAllBeans(){
        log.info("byNameBeans:");
        byNameBeans.forEach((k, v) -> log.info("{} - {}", k, v));
        log.info("byTypeBeans:");
        byTypeBeans.forEach((k, v) -> log.info("{} - {}", k, v));
    }

    private void instance(BeanDefinition def) {

        if(def.byType()){
            if(byTypeBeans.containsKey(def.getClz())){
                return;
            }
        } else {
            if(byNameBeans.containsKey(def.getBeanName())){
                return;
            }
        }


        log.info("when instance : {}", def.getBeanName());

        Object bean;
        if(!def.instanceByCreation()){
            bean = ReflectKit.newInstance(def.getClz());
        } else {
            bean = ReflectKit.creationInvoke(def.getCreation(), def.getProxy());
        }

        autowire(bean);

        addByTypeBean(bean.getClass(), bean);
        if(!Strings.isNullOrEmpty(def.getBeanName())){
            addByNameBean(def.getBeanName(), bean);
        }

    }

    private void autowire(Object bean) {
        Field[] fields = bean.getClass().getFields();
        if(fields != null && fields.length > 0) {
            for (Field field : fields) {
                Autowire a;
                if((a = field.getAnnotation(Autowire.class)) == null)
                    continue;
                if(!Strings.isNullOrEmpty(a.name())){
                    if(byNameBeans.containsKey(a.name())){
                        inject(bean, field, byNameBeans.get(a.name()));
                        continue;
                    }
                    if(byNameDefinitions.containsKey(a.name())){
                        instance(byNameDefinitions.get(a.name()));
                        inject(bean, field, byNameBeans.get(a.name()));
                        continue;
                    }
                    throw new NotFoundBeanDefinitionException(a.name());
                } else {
                    if(byTypeBeans.containsKey(field.getType())){
                        inject(bean, field, byTypeBeans.get(field.getType()));
                        continue;
                    }
                    if(byTypeDefinitions.containsKey(field.getType())){
                        instance(byTypeDefinitions.get(field.getType()));
                        inject(bean, field, byTypeBeans.get(field.getType()));
                        continue;
                    }
                    throw new NotFoundBeanDefinitionException(field.getType().getCanonicalName());
                }
            }
        }
    }

    private void inject(Object obj, Field field, Object value) {
        field.setAccessible(true);
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new FieldInjectorFailedException(e);
        }
    }

    public void addByTypeBean(Class<?> clz, Object bean) {
        if(!byTypeBeans.containsKey(clz)){
            byTypeBeans.put(clz, bean);
        }
    }

    public void addByNameBean(String beanName, Object bean) {
        if(byNameBeans.containsKey(beanName)){
            throw new BeanNameConflictException(beanName);
        }
        byNameBeans.put(beanName, bean);
    }

    private void createBeanDefinition() {
        for (Class<?> clz : configurationSet) {
            BeanDefinition def = new BeanDefinition(clz);
            addByTypeDefinition(clz, def);

            Method[] methods = clz.getDeclaredMethods();
            if(methods != null && methods.length > 0){
                for (Method method : methods) {
                    Bean b;
                    if((b = method.getAnnotation(Bean.class)) == null) {
                        continue;
                    }
                    BeanDefinition def2;
                    if(Strings.isNullOrEmpty(b.name())){
                        def2 = new BeanDefinition(CglibKit.createProxy(clz), method);
                    } else  {
                        def2 = new BeanDefinition(b.name(), CglibKit.createProxy(clz), method);
                    }
                    addByTypeDefinition(method.getReturnType(), def2);
                    if(!Strings.isNullOrEmpty(b.name())){
                        addByNameDefinition(b.name(), def2);
                    }
                }
            }
        }

        for (Class<?> clz : componentSet) {
            Component c = clz.getAnnotation(Component.class);
            BeanDefinition def;
            if(Strings.isNullOrEmpty(c.name())){
                def = new BeanDefinition(clz);
            } else {
                def = new BeanDefinition(c.name(), clz);
            }
            addByTypeDefinition(clz, def);
            if(!Strings.isNullOrEmpty(c.name())){
                addByNameDefinition(c.name(), def);
            }
        }
    }

    private void addByNameDefinition(String beanName, BeanDefinition def) {
        if(byNameDefinitions.containsKey(beanName)){
            throw new BeanNameConflictException(beanName);
        }
        System.out.println(beanName + ":" + def.getBeanName());
        byNameDefinitions.put(beanName, def);
    }

    private void addByTypeDefinition(Class<?> clz, BeanDefinition def) {
        if(!byTypeDefinitions.containsKey(clz)){
            byTypeDefinitions.put(clz, def);
        }
    }

    private void classify() {
        for (Class<?> clz : all) {
            if(ReflectKit.has(Configuration.class, clz))
                configurationSet.add(clz);
            if(ReflectKit.has(Component.class, clz))
                componentSet.add(clz);
        }
    }

    private void discover(String[] args) {
        for (String arg : args) {
            Set<String> set = ReflectKit.scan(arg);
            all.addAll(set.stream().map(s -> ReflectKit.forName(s)).collect(Collectors.toSet()));
        }
    }
}
