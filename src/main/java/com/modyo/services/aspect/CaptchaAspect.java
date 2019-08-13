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

  private static final String CAPTCHA_HEADER_NAME = "captcha-response";
  @Autowired
  private CaptchaValidator captchaValidator;

  //  @Around("@annotation(RequiresCaptcha)")
  @Around("@within(com.modyo.services.aspect.RequiresCaptcha) || @annotation(com.modyo.services.aspect.RequiresCaptcha)")
  public Object validateCaptcha(ProceedingJoinPoint joinPoint) throws Throwable {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
        .currentRequestAttributes()).getRequest();
    String captchaResponse = request.getHeader(CAPTCHA_HEADER_NAME);
    boolean isValidCaptcha = captchaValidator.validateCaptcha(captchaResponse);
    if (!isValidCaptcha) {
      throw new ForbiddenException("Invalid captcha");
    }
    return joinPoint.proceed();
  }

}
