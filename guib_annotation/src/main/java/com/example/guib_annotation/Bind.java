package com.example.guib_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version: 1.0
 * @description: Bind注解 指明注解作用域为类
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Bind {

    int value();

}
