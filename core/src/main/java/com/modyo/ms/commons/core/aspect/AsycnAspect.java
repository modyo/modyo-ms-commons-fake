package com.modyo.ms.commons.core.aspect;

import com.modyo.ms.commons.core.components.InMemoryRequestAttributes;
import com.modyo.ms.commons.core.utils.CorrelationIdUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

@Aspect
@Component
@Order(0)
public class AsycnAspect {

  @Before("@annotation(org.springframework.scheduling.annotation.Async)")
  public void createRequestContext() {
    try {
      RequestContextHolder.currentRequestAttributes();
    } catch (IllegalStateException e) {
      RequestContextHolder.setRequestAttributes(new InMemoryRequestAttributes());
      CorrelationIdUtils.generateCorrelationId();
    }

  }

}
