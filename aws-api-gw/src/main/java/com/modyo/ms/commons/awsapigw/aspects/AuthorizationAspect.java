package com.modyo.ms.commons.awsapigw.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthorizationAspect {

  @Around(value = "@annotation(RequiresLambdaAuthorization)")
  public Object getJwtInformation(ProceedingJoinPoint joinPoint) throws Throwable {
    // TODO capture information from JWT Token
    return joinPoint.proceed();
  }

}
