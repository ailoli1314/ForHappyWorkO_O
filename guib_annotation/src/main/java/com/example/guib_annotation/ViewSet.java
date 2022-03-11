package com.example.guib_annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  辅助类 GuiBhelp ：用于为不变化的 逻辑简单的 view 初始化数据展示,偷懒用的
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
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
