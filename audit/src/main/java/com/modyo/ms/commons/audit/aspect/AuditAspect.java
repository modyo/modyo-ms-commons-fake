package com.modyo.ms.commons.audit.aspect;

import com.modyo.ms.commons.audit.aspect.context.AuditGetContext;
import com.modyo.ms.commons.audit.aspect.context.AuditSetContext;
import com.modyo.ms.commons.audit.service.CreateAuditLogService;
import com.modyo.ms.commons.core.dtos.Dto;
import java.lang.reflect.Method;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
class AuditAspect {

  private final CreateAuditLogService createAuditLogService;

  @Around("@annotation(com.modyo.ms.commons.audit.aspect.ModyoAudit)")
  public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {

    ModyoAudit modyoAudit = getModyoAudit(joinPoint);
    try {
      AuditSetContext.setEventInfo(modyoAudit.prefix(), modyoAudit.changeType(), modyoAudit.event());

      Object result = joinPoint.proceed();
      AuditContextHelper.logSuccess(
          modyoAudit.prefix(),
          createAuditLogService,
          AuditGetContext.getInitialValue(modyoAudit.prefix()),
          AuditGetContext.getNewValue(modyoAudit.prefix()));
      return result;
    } catch (Exception e) {
      AuditContextHelper.logError(modyoAudit.prefix(), createAuditLogService, e);
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
