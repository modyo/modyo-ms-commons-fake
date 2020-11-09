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
  private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

  @Around("@annotation(com.modyo.ms.commons.audit.aspect.ModyoAudit)")
  public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {

    try {
      Object result = joinPoint.proceed();

      logInfo(joinPoint);
      return result;
    } catch (Exception e) {
      logError(joinPoint, e);
      throw e;
    }


  }

  private void logInfo(ProceedingJoinPoint joinPoint) {
    try {
      createAuditLogService.logInfo(
          AuditContext.getChildEntityId(),
          AuditContext.getParentEntityId(),
          AuditContext.getParentEntity(),
          AuditContext.getInitialValue(),
          AuditContext.getNewValue(),
          getModyoAudit(joinPoint).changeType(),
          getModyoAudit(joinPoint).event()
      );
    } catch (Exception e) {
      logger.error("Error in createAuditLogService.logInfo: {}", e.getMessage());
    }
  }

  private void logError(ProceedingJoinPoint joinPoint, Exception exception) {
    try {
      createAuditLogService.logError(
          AuditContext.getChildEntityId(),
          AuditContext.getParentEntityId(),
          AuditContext.getParentEntity(),
          AuditContext.getInitialValue(),
          new ErrorMessageDto(exception.getClass().getName(),
              exception.getMessage(),
              Arrays.toString(exception.getStackTrace())),
          getModyoAudit(joinPoint).changeType(),
          getModyoAudit(joinPoint).event()
      );
    } catch (Exception e) {
      logger.error("Error in createAuditLogService.logError: {}", e.getMessage());
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
