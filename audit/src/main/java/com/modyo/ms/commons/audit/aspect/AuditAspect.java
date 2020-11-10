package com.modyo.ms.commons.audit.aspect;

import com.modyo.ms.commons.audit.service.CreateAuditLogService;
import com.modyo.ms.commons.core.dtos.Dto;
import java.lang.reflect.Method;
import java.util.Arrays;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
class AuditAspect {

  private final CreateAuditLogService createAuditLogService;

  @Around("@annotation(com.modyo.ms.commons.audit.aspect.ModyoAudit)")
  public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {

    try {
      ModyoAudit modyoAudit = getModyoAudit(joinPoint);
      AuditContext.setEventInfo(modyoAudit.changeType(), modyoAudit.event());

      Object result = joinPoint.proceed();
      AuditContextHelper.logInfo(createAuditLogService, AuditContext.getInitialValue(), AuditContext.getNewValue());
      return result;
    } catch (Exception e) {
      AuditContextHelper.logError(createAuditLogService, e);
      throw e;
    }


  }

  private ModyoAudit getModyoAudit(ProceedingJoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    return method.getAnnotation(ModyoAudit.class);
  }

  @EqualsAndHashCode(callSuper = true)
  @Data
  static class ErrorMessageDto extends Dto {
    private final String type;
    private final String message;
    private final String stackTrace;
  }


}
