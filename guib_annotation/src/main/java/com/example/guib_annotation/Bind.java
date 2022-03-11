package com.example.guib_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Created by qiyei2015 on 2018/4/14.
 * @version: 1.0
 * @email: 1273482124@qq.com
 * @description: Bind注解 指明注解作用域为类
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Bind {

    int value();

}
