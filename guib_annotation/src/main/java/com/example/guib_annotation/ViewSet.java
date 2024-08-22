package com.example.guib_annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  imageview，textview填充简单内容
 *  图片值传入url、drawable
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ViewSet {
    int id() default 0x00;
    String head() default "";//textview 的头部信息 例如：￥ 等
    String foot() default "";// 尾部信息
    ViewType type() default ViewType.TV;// imageview 标识

    enum  ViewType {
        IV,TV
    }
}
