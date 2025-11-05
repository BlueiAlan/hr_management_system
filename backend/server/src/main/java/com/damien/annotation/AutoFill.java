package com.damien.annotation;

import com.damien.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识需要执行自动填充的逻辑
 */
@Target(ElementType.METHOD)    // 注解作用范围：方法
@Retention(RetentionPolicy.RUNTIME)    // 注解保留策略：运行时（使用@Retention必须要为value指定成员变量的值，该成员变量为一个容器注解，这个容器注解包含多个OperationType）
public @interface AutoFill {
    /**
     * 数据库操作类型：INSERT、UPDATE、DELETE、SELECT
     * @return  数据库操作类型
     */
    OperationType value();
}
