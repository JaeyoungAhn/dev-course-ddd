package com.programmers.com.kdtspringorder.aop;

import org.aspectj.lang.annotation.Pointcut;

public class CommonPointCut {
    @Pointcut("execution(public * com.programmers.com.kdtspringorder..*Service.*(..))")
    public void servicePublicMethodPointcut() {}

    @Pointcut("execution(* com.programmers.com.kdtspringorder..*Repository.*(..))")
    public void repositoryMethodPointcut() {}

    @Pointcut("execution(* com.programmers.com.kdtspringorder..*Repository.insert(..))")
    public void repositoryInsertMethodPointcut() {}
}
