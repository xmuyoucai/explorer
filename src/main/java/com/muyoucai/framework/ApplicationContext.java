package com.muyoucai.framework;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.muyoucai.config.AppConfig;
import com.muyoucai.framework.annotation.*;
import com.muyoucai.ex.CustomException;
import com.muyoucai.FrontEntrance;
import com.muyoucai.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ApplicationContext {

    public static final FrontEntrance FRONT_ENTRANCE = new FrontEntrance();

    private static final Map<String, BeanDefinition> DEFINITIONS = Maps.newHashMap();
    private static final Map<String, Object> BY_TYPE_BEANS = Maps.newHashMap();
    private static final Map<String, Object> BY_NAME_BEANS = Maps.newHashMap();

    public static void main(String[] args) {
        run(args);
    }

    public static void run(String[] args) {

        BY_TYPE_BEANS.put(Environment.class.getCanonicalName(), new Environment());

        String[] scanPackages = new String[]{"com.muyoucai.config"};

        Set<Class<?>> configurationSet = Sets.newHashSet();
        for (String scanPackage : scanPackages) {
            Set<String> set = ReflectKit.scan(scanPackage);
            configurationSet.addAll(set.stream().map(x -> ReflectKit.forName(x)).filter(clz -> ReflectKit.has(Configuration.class, clz)).collect(Collectors.toSet()));
        }

        getBean(AppConfig.MMM.class);
        BY_TYPE_BEANS.forEach((k, w) -> log.info("{} - {}", k, w.getClass().getCanonicalName()));

    }

    public static <T> T getBean2(Class<T> clz) {
        return (T) BY_TYPE_BEANS.get(clz.getCanonicalName());
    }

    public static <T> T getBean(Class<T> clz) {
        Object bean = BY_TYPE_BEANS.get(clz.getCanonicalName());
        if (bean != null) {
            log.info("Direct return cached bean : {}", clz.getCanonicalName());
            return (T) bean;
        }
        BeanDefinition def = DEFINITIONS.get(clz.getCanonicalName());
        if (def != null) {
            if (def.getCreation() != null && def.getProxy() != null) {
                return (T) ReflectKit.creationInvoke(def.getCreation(), def.getProxy());
            }
        }
        throw new CustomException(String.format("not found bean definition of %s", clz.getCanonicalName()));
    }

    private static void instantiate(String className) {

        // 如果已经包含该 BEAN ，则直接返回
        if (BY_TYPE_BEANS.containsKey(className) || DEFINITIONS.containsKey(className)) return;

        // 获取 Class 对象
        Class<?> clz = ReflectKit.forName(className);

        // 处理 @Component
        if (ReflectKit.has(Component.class, clz)) {
            log.info("@Component on {}", clz.getCanonicalName());
            Component component = clz.getAnnotation(Component.class);
            Object bean = createByType(clz);
            if (!Strings.isNullOrEmpty(component.name())) {
                cacheBeanByName(component.name(), bean);
            }
        }

        // 处理 @Configuration
        if (ReflectKit.has(Configuration.class, clz)) {
            log.info("@Configuration on {}", clz.getCanonicalName());
            Configuration configuration = clz.getAnnotation(Configuration.class);
            Object bean = createByType(clz);
            // 处理 @Bean 注解
            Method[] methods = clz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getAnnotation(Bean.class) == null) continue;
                log.info("@Bean on {}.{}()", clz.getCanonicalName(), method.getName());
                DEFINITIONS.put(method.getReturnType().getCanonicalName(), new BeanDefinition(bean, method));
            }
        }

    }

    private static Object createByType(Class<?> clz) {
        Object bean = CglibKit.createProxy(clz);
        cacheBeanByType(clz, bean);
        return bean;
    }

    public static void cacheBeanByType(Class<?> clz, Object bean) {
        log.info("Cached bean by type : {}", clz.getCanonicalName());
        BY_TYPE_BEANS.put(clz.getCanonicalName(), bean);
    }

    public static void cacheBeanByName(String beanName, Object bean) {
        log.info("Cached bean by name for {} : {}", bean.getClass().getCanonicalName(), beanName);
        BY_NAME_BEANS.put(beanName, bean);
    }

    private static void autowireFields(Object bean) throws InstantiationException, IllegalAccessException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            Autowire mAutowire = field.getAnnotation(Autowire.class);
            if (mAutowire != null) {
                autowire(bean, field);
            }
            Value mValue = field.getAnnotation(Value.class);
            if (mValue != null) {
                field.setAccessible(true);
                Environment environment = (Environment) BY_TYPE_BEANS.get(Environment.class.getCanonicalName());
                field.set(bean, environment.get(mValue.value()));
            }
        }
    }

    private static void autowire(Object bean, Field field) throws InstantiationException, IllegalAccessException {
        //
        instantiate(field.getType().getCanonicalName());
        //
        Object injectorObj;
        //
        if ((injectorObj = BY_TYPE_BEANS.get(field.getType().getCanonicalName())) == null)
            throw new InstantiationException(String.format("no bean of %s found", field.getType().getCanonicalName()));
        //
        field.setAccessible(true);
        field.set(bean, injectorObj);
    }

}
