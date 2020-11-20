package com.modyo.ms.commons.audit.aspect;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;

import com.modyo.ms.commons.audit.AuditLogType;
import com.modyo.ms.commons.audit.aspect.context.AuditSetContext;
import com.modyo.ms.commons.audit.service.CreateAuditLogService;
import com.modyo.ms.commons.core.components.InMemoryRequestAttributes;
import com.modyo.ms.commons.http.loggers.RestTemplateRequestLogger;
import com.modyo.ms.commons.http.loggers.RestTemplateResponseLogger;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;

class RestTemplateAuditInterceptorServiceTest {

  private final CreateAuditLogService createAuditLogService
      = Mockito.mock(CreateAuditLogService.class);

  private final Object parentEntity = new Object();
  private final Object childEntityBefore = new Object();
  private final Object childEntityAfter = new Object();
  private final String parentEntityId = "pid";
  private final String childEntityId = "cid";

  private final RestTemplateAuditInterceptorService serviceUnderTest
      = new RestTemplateAuditInterceptorService(createAuditLogService);

  @BeforeEach
  void setUp() {
    RequestContextHolder.setRequestAttributes(new InMemoryRequestAttributes());
    AuditSetContext.setEventInfo("", "CHANGE_STATUS", "GENERAL_EVENT");
  }

  @Test
  void log_whenHttpEventExists_ThenSetThis() {
    AuditSetContext.setParentEntityAndInitialInfo("", parentEntity, parentEntityId, childEntityBefore, childEntityId);
    AuditSetContext.setNewValue("", childEntityAfter);
    AuditSetContext.setHttpEventInfo("HTTP_EVENT_NAME");

    serviceUnderTest.intercept(
        new RestTemplateRequestLogger(
            null, null, new HttpHeaders(), null, Collections.emptyList()),
        new RestTemplateResponseLogger(null, new HttpHeaders(), null, null));
    then(createAuditLogService).should().log(eq(AuditLogType.INFO),
        eq(childEntityId), eq(parentEntityId), eq(parentEntity),
        any(RestTemplateRequestLogger.class), any(RestTemplateResponseLogger.class),
        eq("http_request"), eq("HTTP_EVENT_NAME")
    );

  }

  @Test
  void log_whenNoHttpEventExists_ThenSetGeneralEventName() {
    AuditSetContext.setParentEntityAndInitialInfo("", parentEntity, parentEntityId, childEntityBefore, childEntityId);
    AuditSetContext.setNewValue("", childEntityAfter);

    serviceUnderTest.intercept(
        new RestTemplateRequestLogger(
            null, null, new HttpHeaders(), null, Collections.emptyList()),
        new RestTemplateResponseLogger(null, new HttpHeaders(), null, null));
    then(createAuditLogService).should().log(eq(AuditLogType.INFO),
        eq(childEntityId), eq(parentEntityId), eq(parentEntity),
        any(RestTemplateRequestLogger.class), any(RestTemplateResponseLogger.class),
        eq("http_request"), eq("GENERAL_EVENT")
    );

  }

  @Test
  void audit_WhenLogInfoFails_ThenDoNotThrowException() {
    AuditSetContext.setParentEntityAndInitialInfo("", parentEntity, parentEntityId, childEntityBefore, childEntityId);
    AuditSetContext.setNewValue("", childEntityAfter);
    doThrow(IllegalArgumentException.class).when(createAuditLogService)
        .log(any(), anyString(), anyString(), any(), any(), any(), any(), anyString());

    serviceUnderTest.intercept(
        new RestTemplateRequestLogger(
            null, null, new HttpHeaders(), null, Collections.emptyList()),
        new RestTemplateResponseLogger(null, new HttpHeaders(), null, null));
    then(createAuditLogService).should().log(eq(AuditLogType.INFO),
        eq(childEntityId), eq(parentEntityId), eq(parentEntity),
        any(RestTemplateRequestLogger.class), any(RestTemplateResponseLogger.class),
        eq("http_request"), eq("GENERAL_EVENT")
    );

  }

}
