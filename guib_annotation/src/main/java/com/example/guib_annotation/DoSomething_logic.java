package com.example.guib_annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解 method 实现某些逻辑复杂的操作，例：在
 * 被注解 的method 应当带有唯一 的参数 用于获取 上下文 View (context)，或者当前类 已继承context_if 接口（注解 setContext），使用get方法获取上下文
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface DoSomething_logic {
    String value() default "nothing";
}