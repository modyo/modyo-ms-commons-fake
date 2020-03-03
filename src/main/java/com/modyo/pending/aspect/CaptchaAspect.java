package com.modyo.pending.aspect;

import com.modyo.services.capcha.CaptchaValidator;
import com.modyo.services.capcha.model.CaptchaResponse;
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
    String captchaResponse = request.getHeader("captcha-response") == null ?
        "" : request.getHeader("captcha-response");
    String xCaptchaResponse = request.getHeader("X-Captcha-Response") == null ?
        "" : request.getHeader("X-Captcha-Response");
    CaptchaResponse isValidCaptcha = captchaValidator
        .validateCaptcha(captchaResponse + xCaptchaResponse);
    if (!isValidCaptcha.getSuccess()) {
      if (isValidCaptcha.getScore() == null) {
        isValidCaptcha.setScore(0f);
      }
      throw new ForbiddenException(exceptionCaptchaText(isValidCaptcha.getScore().toString(),
          isValidCaptcha.getErrorCodes().get(0),
          captchaResponse,
          xCaptchaResponse
      ));
    }
    return joinPoint.proceed();
  }

  private String exceptionCaptchaText(String score,
      String errorCode,
      String captchaResponse,
      String xCaptchaResponse) {
    StringBuilder result = new StringBuilder();
    result.append("{");
    result.append("score: " + score + ",");
    result.append("errorCode: " + errorCode + ",");
    result.append("captcha-response: " + captchaResponse + ",");
    result.append("X-Captcha-Response: " + xCaptchaResponse);
    result.append("}");
    return result.toString();
  }

}
