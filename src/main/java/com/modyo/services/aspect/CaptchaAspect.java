package com.modyo.services.aspect;

import com.modyo.services.capcha.CaptchaValidator;
import com.modyo.services.exceptions.ForbiddenException;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@ConditionalOnBean(CaptchaValidator.class)
public class CaptchaAspect {

  @Autowired
  private CaptchaValidator captchaValidator;

  @Around("@within(RequiresCaptcha) || @annotation(RequiresCaptcha)")
  public Object validateCaptcha(ProceedingJoinPoint joinPoint) throws Throwable {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
        .currentRequestAttributes()).getRequest();
    //TODO: Deprecate "captcha-response" and apply "X-Captcha-Response"
    String captchaResponse = request.getHeader("captcha-response") + request.getHeader("X-Captcha-Response");
    boolean isValidCaptcha = captchaValidator.validateCaptcha(captchaResponse);
    if (!isValidCaptcha) {
      throw new ForbiddenException("Invalid captcha");
    }
    return joinPoint.proceed();
  }

}
