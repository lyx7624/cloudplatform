package com.zcyk.util;


import com.zcyk.annotation.Log;
import com.zcyk.service.LogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 功能描述: 日志处理类
 * 版本信息: Copyright (c)2019
 * 公司信息: 智辰云科
 * 开发人员: lyx
 * 版本日志: 1.0
 * 创建日期: 2019/8/12 10:50
 */
@Aspect
@Component
public class LogAspect {

    @Autowired
    private LogService logService;


    /**
     * 日志切入点
     */
    @Pointcut("@annotation(com.zcyk.annotation.Log)")
    public void logPointCut() {
    }

    @AfterReturning(pointcut = "logPointCut()")
    public void doAfter(JoinPoint joinPoint) {
        /**
         * 解析Log注解
         */
        Signature signature = joinPoint.getSignature();
        String name = signature.getName();
        Object target = joinPoint.getTarget();
        String s = target.toString();
        Object[] args = joinPoint.getArgs();
        String conment = "";
        for (Object o : args) {
            conment += o.toString();
        }
        String methodName = joinPoint.getSignature().getName();
        Method method = currentMethod(joinPoint, methodName);
        Log log = method.getAnnotation(Log.class);
        logService.put(conment, methodName, log.module(), log.description());
    }

    /**
     * 获取当前执行的方法
     *
     * @param joinPoint  连接点
     * @param methodName 方法名称
     * @return 方法
     */
    private Method currentMethod(JoinPoint joinPoint, String methodName) {
        /**
         * 获取目标类的所有方法，找到当前要执行的方法
         */
        Method[] methods = joinPoint.getTarget().getClass().getMethods();
        Method resultMethod = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                resultMethod = method;
                break;
            }
        }
        return resultMethod;
    }

}
