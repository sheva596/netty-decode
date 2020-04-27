package com.centerm.nettydecode.aspect;

import com.centerm.nettydecode.pojo.SysLog;
import com.centerm.nettydecode.service.SysService;
import com.centerm.nettydecode.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author Sheva
 * @date 2020/4/23 11:49
 * @description
 */
@Component
@Aspect
@Slf4j
public class LogAspect {

    @Autowired
    private SysService sysService;


    public LogAspect(SysService sysService) {
        this.sysService = sysService;
    }

    /**
     * 配置切入点
     */
    @Pointcut("@annotation(com.centerm.nettydecode.aop.log.Log)")
    public void logPointcut() {
    }

    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        long beginTime = System.currentTimeMillis();
        result = joinPoint.proceed();
        long time = System.currentTimeMillis() - beginTime;
        saveLog(joinPoint, time);
        return result;
    }

    /**
     * 配置异常通知
     * @param joinPoint join point for advice
     * @param e         exception
     */
    @AfterThrowing(pointcut = "logPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLog errLog = new SysLog();
        errLog.setExceptionDetail(LogUtils.getStackTrace(e).getBytes());
        HttpServletRequest request = LogUtils.getHttpServletRequest();
        errLog.setReqIp(LogUtils.getIp(request));
        errLog.setUsername("admin");
        errLog.setLogType("ERROR");
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        errLog.setMethod(className + "." + methodName + "()");
        errLog.setDescription(method.getAnnotation(com.centerm.nettydecode.aop.log.Log.class).value());
        sysService.saveSysLog(errLog);
    }

    private void saveLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLog sysLog = new SysLog();
        // 请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");
        // 请求的方法参数值
        Object[] args = joinPoint.getArgs();
        // 请求的方法参数名称
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(method);
        if (args != null && paramNames != null) {
            String params = "";
            for (int i = 0; i < args.length; i++) {
                params += "  " + paramNames[i] + ": " + args[i];
            }
            sysLog.setParams(params);
        }
        // 获取request
        HttpServletRequest request = LogUtils.getHttpServletRequest();
        // 设置IP地址
        sysLog.setReqIp(LogUtils.getIp(request));
        //设置级别和操作名称
        sysLog.setLogType("INFO");
        sysLog.setDescription(method.getAnnotation(com.centerm.nettydecode.aop.log.Log.class).value());
        // 模拟一个用户名
        sysLog.setUsername("admin");
        sysLog.setExecuteTime(time);
        // 保存系统日志
        sysService.saveSysLog(sysLog);
    }
}
