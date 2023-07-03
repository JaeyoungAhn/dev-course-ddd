package com.programmers.com.kdtspringorder.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);


    //("execution(접근제어자 리턴타입 패키지명 (생략)..*.(메소드)*(인자))"
//    @Around("execution(public * com.programmers.com.kdtspringorder..*Service.*(..))")


//    @Around("com.programmers.com.kdtspringorder.aop.CommonPointCut.repositoryInsertMethodPointcut()")
    @Around("@annotation(com.programmers.com.kdtspringorder.aop.TrackTime)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {

        log.info("Before method called. {}", joinPoint.getSignature().toString());
        var startTime = System.nanoTime(); // 1 -> 1,000,000,000
        var result = joinPoint.proceed();
        var endTime = System.nanoTime() - startTime;
        log.info("After method called with result => {} and time taken {} nanoseconds", result, endTime);
        return result;
    }
}
