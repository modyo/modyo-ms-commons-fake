package com.modyo.ms.commons.audit.aspect;

import com.modyo.ms.commons.audit.service.CreateAuditLogService;
import com.modyo.ms.commons.http.config.RestTemplateInterceptorService;
import com.modyo.ms.commons.http.loggers.RestTemplateRequestLogger;
import com.modyo.ms.commons.http.loggers.RestTemplateResponseLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class RestTemplateAuditInterceptorService implements RestTemplateInterceptorService {

  private final CreateAuditLogService createAuditLogService;

  @Override
  public void intercept(RestTemplateRequestLogger requestLogger, RestTemplateResponseLogger responseLogger) {
    AuditContextHelper.logInfo(createAuditLogService, requestLogger, responseLogger);
  }
}
