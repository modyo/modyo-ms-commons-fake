package com.modyo.ms.commons.core.aspect;

import com.modyo.ms.commons.core.components.InMemoryRequestAttributes;
import com.modyo.ms.commons.core.loggers.TaskLogger;
import com.modyo.ms.commons.core.utils.CorrelationIdUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

@Aspect
@Component
public class TaskAspect {

  @Around("@annotation(org.springframework.scheduling.annotation.Scheduled)")
  public Object logTasks(ProceedingJoinPoint joinPoint) throws Throwable {
    RequestContextHolder.setRequestAttributes(new InMemoryRequestAttributes());
    String taskName = joinPoint.getSignature().toShortString();
    CorrelationIdUtils.generateCorrelationId();
    TaskLogger.start(taskName).logInfo();
    try {
      Object object = joinPoint.proceed();
      TaskLogger.end(taskName).logInfo();
      return object;
    } catch (Exception e) {
      TaskLogger.error(taskName, e.getMessage()).logError();
      return null;
    }

  }

}
