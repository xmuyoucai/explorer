package com.muyoucai.framework;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.muyoucai.framework.annotation.LzyAutowired;
import com.muyoucai.framework.annotation.LzyBean;
import com.muyoucai.framework.annotation.LzyConfiguration;
import com.muyoucai.util.CollectionKit;
import com.muyoucai.util.ex.*;
import com.muyoucai.framework.annotation.LzyComponent;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
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
    private List<BeanDefinition> definitions = Lists.newArrayList();
    private Map<String, BeanDefinition> byNameDefinitions = Maps.newHashMap();
    private Map<Class<?>, BeanDefinition> byTypeDefinitions = Maps.newHashMap();


    public BeanFactory(String ... args) {
        discover(args);
        classify();
        createBeanDefinition();
    }

    public boolean containsBean(Class<?> clz){
        if(byTypeDefinitions.containsKey(clz)){
            if(byTypeDefinitions.get(clz).isInstanced()){
                return true;
            }
        }
        return false;
    }

    public Object getBeanByType(Class<?> clz){
        if(containsBean(clz)){
            return byTypeDefinitions.get(clz).getBean();
        }
        return null;
    }

    public void createAllBeans() {
        definitions.forEach(def -> instance(def));
    }

    public void displayAllBeans(){
        log.info("byNameBeans:");
        byNameDefinitions.forEach((k, v) -> log.info("{} - {}", k, v.getBean()));
        log.info("byTypeBeans:");
        byTypeDefinitions.forEach((k, v) -> log.info("{} - {}", k, v.getBean()));
    }

    private void instance(BeanDefinition def) {

        log.info("handle : <{}, {}>", def.getName(), def.getClz());

        if(def.isInstanced()){
            log.debug("def has instanced, skip : {}", def.getClz().getCanonicalName());
            return;
        }

        if(!def.instanceByCreation()){
            def.setBean(CglibKit.createProxy(def.getClz()));
        } else {
            if(!containsBean(def.getFromClz())){
                if(!byTypeDefinitions.containsKey(def.getFromClz())){
                    throw new NotFoundBeanDefinitionException(def.getFromClz().getCanonicalName());
                }
                instance(byTypeDefinitions.get(def.getFromClz()));
            }
            Object fromBean = getBeanByType(def.getFromClz());
            def.setBean(ReflectKit.creationInvoke(def.getCreation(), fromBean));
        }
        Set<Field> fields = ReflectKit.getAllFields(def.getClz());
        log.info("{} : {} : ", def.getClz(), def.getBean());
        if(!CollectionKit.isEmpty(fields)){
            autowire(def.getBean(), def.getClz(), fields);
        } else {
            log.info("{} no fields", def.getClz());
        }
        def.setInstanced(true);
    }

    private void autowire(Object bean, Class<?> clz, Set<Field> fields) {
        for (Field field : fields) {
            // log.info("{} : {} : {} : {}", clz.getCanonicalName(), bean.getClass(), field.getName(), ReflectKit.has(LzyAutowired.class, field));
            if(!ReflectKit.has(LzyAutowired.class, field)){
                continue;
            }
            LzyAutowired a = field.getAnnotation(LzyAutowired.class);
            if(!Strings.isNullOrEmpty(a.name())){
                if(byNameDefinitions.containsKey(a.name())){
                    BeanDefinition def = byNameDefinitions.get(a.name());
                    if(!def.isInstanced()){
                        instance(def);
                    }
                    inject(bean, field, def.getBean());
                    continue;
                }
                throw new NotFoundBeanDefinitionException(a.name());
            } else {
                if(byTypeDefinitions.containsKey(field.getType())){
                    BeanDefinition def = byTypeDefinitions.get(field.getType());
                    if(!def.isInstanced()){
                        instance(def);
                    }
                    inject(bean, field, def.getBean());
                    continue;
                }
                throw new NotFoundBeanDefinitionException(field.getType().getCanonicalName());
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

    private void createBeanDefinition() {
        for (Class<?> clz : configurationSet) {
            addDefinition("", clz, new BeanDefinition("", clz, null, null, null));
            Set<Method> methods = ReflectKit.getAllMethods(clz);
            if(!CollectionKit.isEmpty(methods)){
                for (Method method : methods) {
                    if(!ReflectKit.has(LzyBean.class, method)) {
                        continue;
                    }
                    LzyBean b = method.getAnnotation(LzyBean.class);
                    BeanDefinition def = new BeanDefinition(b.name(), method.getReturnType(), clz, method, null);
                    addDefinition(b.name(), method.getReturnType(), def);
                }
            }
        }
        for (Class<?> clz : componentSet) {
            LzyComponent c = clz.getAnnotation(LzyComponent.class);
            BeanDefinition def = new BeanDefinition(c.name(), clz, null, null, null);
            addDefinition(c.name(), clz, def);
        }
    }

    private void addDefinition(String name, Class<?> clz, BeanDefinition def) {
        log.info("Add BeanDefinition : <{}, {}>", name, clz);
        if(!Strings.isNullOrEmpty(name)){
            addByNameDefinition(name, def);
        }
        addByTypeDefinition(clz, def);
        definitions.add(def);
    }

    private void addByNameDefinition(String beanName, BeanDefinition def) {
        if(byNameDefinitions.containsKey(beanName)){
            throw new BeanNameConflictException(beanName);
        }
        byNameDefinitions.put(beanName, def);
    }

    private void addByTypeDefinition(Class<?> clz, BeanDefinition def) {
        if(!byTypeDefinitions.containsKey(clz)){
            byTypeDefinitions.put(clz, def);
        }
    }

    private void classify() {
        for (Class<?> clz : all) {
            if(ReflectKit.has(LzyConfiguration.class, clz))
                configurationSet.add(clz);
            if(ReflectKit.has(LzyComponent.class, clz))
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
