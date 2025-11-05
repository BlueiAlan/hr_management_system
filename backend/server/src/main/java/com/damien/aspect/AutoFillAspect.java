package com.damien.aspect;

import com.damien.annotation.AutoFill;
import com.damien.constant.AutoFillConstant;
import com.damien.context.BaseContext;
import com.damien.enumeration.OperationType;
import com.damien.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * 切面类，用于实现自动填充功能
 *
 * 1. 前置通知，在方法执行前执行，对数据进行填充
 * 2. 后置通知，在方法执行后执行，对数据进行填充
 * 3. 环绕通知，在方法执行前后都执行，对数据进行填充
 * 4. 异常通知，在方法执行过程中发生异常时执行，对数据进行填充
 * 5. 最终通知，无论方法是否发生异常，都会执行，对数据进行填充
 */
@Aspect    // 切面类
@Component
@Slf4j
public class AutoFillAspect {
    // 定义切点
    // execution(返回值类型 包名.类名.方法名(参数列表))
    //  --> * 表示任意返回值类型
    //  --> com.sky.mapper.* 表示com.sky.mapper包下的任意类
    //  --> *(..) 表示任意方法名，任意参数列表
    // @annotation(com.sky.annotation.AutoFill) 表示方法上有@AutoFill注解
    @Pointcut("execution(* com.damien.mapper.*.*(..)) && @annotation(com.damien.annotation.AutoFill)")
    public void pointcut() {}

    /**
     * 前置通知，在方法执行前执行，对数据进行填充
     * @param joinPoint  连接点，用于获取方法信息
     */
    @Before("pointcut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段数据填充...");

        // 获取拦截的方法上操作数据类型
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature(); // 向下转型获取方法签名
        AutoFill autoFill = methodSignature.getMethod().getAnnotation(AutoFill.class); // 获取方法上的@AutoFill注解
        OperationType operationType = autoFill.value(); // 获取注解上的数据库操作类型

        // 获取方法参数
        Object[] args = joinPoint.getArgs(); // 获取方法参数
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0]; // 获取第一个参数

        // 准备赋值数据
        LocalDateTime now = LocalDateTime.now();
        Integer currentId = BaseContext.getCurrentId();

        // 根据不同操作类型，为对应的属性通过反射赋值
        try {
            if (operationType == OperationType.INSERT){

                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class).invoke(entity, now);
                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class).invoke(entity, now);
//                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class).invoke(entity, currentId);
//                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class).invoke(entity, currentId);

            } else if (operationType == OperationType.UPDATE) {
                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class).invoke(entity, now);
//                entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class).invoke(entity, currentId);
            }
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            throw new BaseException();
        }
    }
}
