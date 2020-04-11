package com.muyoucai.framework.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface LzyAutowired {

    String name() default "";

}
