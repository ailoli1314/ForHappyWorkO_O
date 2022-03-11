package com.example.guib_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 *  配合 Context_IF 接口 使用，用于设置上下文（反射 Context_IF 中 view，activity，CONTEXT 设置上下文）
 *  主要用于 soquickadapter 万能适配器，其他的地方也可以 但不必要。 例如：在bean类中获取上下文 进行各种操作
 */

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface setContext {
    int value() default -1;
}
