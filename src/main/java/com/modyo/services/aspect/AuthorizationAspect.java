package com.modyo.services.aspect;

import com.modyo.services.capcha.CaptchaValidator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnBean(CaptchaValidator.class)
public class AuthorizationAspect {

  @Around(value = "@annotation(RequiresLambdaAuthorization)")
  public Object getJwtInformation(ProceedingJoinPoint joinPoint) throws Throwable {
    // TODO capture information from JWT Token
    return joinPoint.proceed();
  }

}
