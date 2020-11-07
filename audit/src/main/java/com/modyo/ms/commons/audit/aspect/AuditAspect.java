package com.modyo.ms.commons.audit.aspect;

import com.modyo.ms.commons.audit.service.CreateAuditLogService;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

  private final CreateAuditLogService createAuditLogService;

  @Around("@annotation(com.modyo.ms.commons.audit.aspect.ModyoAudit)")
  public Object clearCache(ProceedingJoinPoint joinPoint) throws Throwable {

    try {
      Object result = joinPoint.proceed();
      createAuditLogService.logInfo(
          AuditContext.getParentEntityId(),
          AuditContext.getParentEntity(),
          AuditContext.getParentEntity(),
          getModyoAudit(joinPoint).changeType(),
          getModyoAudit(joinPoint).event()
      );

      return result;
    } catch (Exception e) {
      createAuditLogService.logError(
          AuditContext.getParentEntityId(),
          AuditContext.getParentEntity(),
          AuditContext.getParentEntity(),
          getModyoAudit(joinPoint).changeType(),
          getModyoAudit(joinPoint).event()
      );
      throw e;
    }


  }

  private ModyoAudit getModyoAudit(ProceedingJoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    return method.getAnnotation(ModyoAudit.class);
  }


}
