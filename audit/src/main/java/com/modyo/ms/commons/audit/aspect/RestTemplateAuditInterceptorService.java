package com.modyo.ms.commons.audit.aspect;

import com.modyo.ms.commons.audit.aspect.context.AuditContext;
import com.modyo.ms.commons.audit.aspect.context.AuditGetContext;
import com.modyo.ms.commons.audit.aspect.context.AuditSetContext;
import com.modyo.ms.commons.audit.service.CreateAuditLogService;
import com.modyo.ms.commons.core.dtos.Dto;
import com.modyo.ms.commons.http.interceptors.RestTemplateInterceptorService;
import com.modyo.ms.commons.http.loggers.RestTemplateRequestLogger;
import com.modyo.ms.commons.http.loggers.RestTemplateResponseLogger;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class RestTemplateAuditInterceptorService implements RestTemplateInterceptorService {

  private final CreateAuditLogService createAuditLogService;

  @Override
  public void intercept(RestTemplateRequestLogger requestLogger, RestTemplateResponseLogger responseLogger) {

    String eventName = Optional.ofNullable(AuditSetContext.resetHttpEventInfo())
        .orElseGet(() -> AuditGetContext.getEventName(AuditContext.CURRENT_PREFIX));
    String changeType = Optional.ofNullable(AuditSetContext.resetHttpChangeType())
        .orElse("http_request");

    if(AuditSetContext.resetDisableNextHttpRequest()) {
      return;
    }

    HttpStatus httpStatus = Optional.ofNullable(responseLogger)
        .map(RestTemplateResponseLogger::getStatus)
        .map(HttpStatus::valueOf)
        .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    boolean logRequestAndResponse = AuditSetContext.resetLogRequestAndResponseAlways() ||
        !httpStatus.is2xxSuccessful();
    AuditContextHelper.logInfo(
        AuditContext.CURRENT_PREFIX,
        createAuditLogService,
        logRequestAndResponse ? requestLogger : null,
        logRequestAndResponse ? responseLogger : new AuditResponseDto(httpStatus.value()),
        changeType,
        eventName
        );
  }

  @Data
  @AllArgsConstructor
  static class AuditResponseDto extends Dto {

    private int status;
  }
}
