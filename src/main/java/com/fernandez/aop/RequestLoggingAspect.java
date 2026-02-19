package com.fernandez.aop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RequestLoggingAspect {

    private static final Logger LOG = LoggerFactory.getLogger(RequestLoggingAspect.class);
    
    @Value("${app.log.incoming.pattern}")
    private String incomingPattern;
    
    @Value("${app.log.outgoing.pattern}")
    private String outgoingPattern;

    @Around("execution(* com.fernandez..*Controller.*(..))")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        LOG.info(incomingPattern, joinPoint.getSignature().toShortString(),
                Arrays.toString(joinPoint.getArgs()));
        Object result = joinPoint.proceed();
        LOG.info(outgoingPattern, joinPoint.getSignature().toShortString(), result);
        return result;
    }
}
