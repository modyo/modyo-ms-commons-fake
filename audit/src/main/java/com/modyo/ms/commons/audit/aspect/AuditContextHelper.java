package com.modyo.ms.commons.audit.aspect;

import com.modyo.ms.commons.audit.aspect.AuditAspect.ErrorMessageDto;
import com.modyo.ms.commons.audit.service.ChangeType;
import com.modyo.ms.commons.audit.service.CreateAuditLogService;
import java.util.Arrays;
import java.util.Optional;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;

class AuditContextHelper {

  private static final Logger logger = LoggerFactory.getLogger(AuditContextHelper.class);

  private AuditContextHelper() {

  }

  public static void logSuccess(CreateAuditLogService createAuditLogService, Object initValue, Object newValue) {
    try {
      createAuditLogService.logSuccess(
          AuditContext.getChildEntityId(),
          AuditContext.getParentEntityId(),
          AuditContext.getParentEntity(),
          initValue,
          newValue,
          AuditContext.getChangeType(),
          AuditContext.getEventName()
      );
    } catch (Exception e) {
      logger.error("Error in createAuditLogService.logSuccess: {}", e.getMessage());
    }
  }

  public static void logInfo(CreateAuditLogService createAuditLogService, Object initValue, Object newValue) {
    try {
      createAuditLogService.logInfo(
          AuditContext.getChildEntityId(),
          AuditContext.getParentEntityId(),
          AuditContext.getParentEntity(),
          initValue,
          newValue,
          AuditContext.getChangeType(),
          AuditContext.getEventName()
      );
    } catch (Exception e) {
      logger.error("Error in createAuditLogService.logInfo: {}", e.getMessage());
    }
  }

  public static void logError(CreateAuditLogService createAuditLogService, Exception exception) {
    try {
      createAuditLogService.logError(
          AuditContext.getChildEntityId(),
          AuditContext.getParentEntityId(),
          AuditContext.getParentEntity(),
          AuditContext.getInitialValue(),
          new ErrorMessageDto(exception.getClass().getName(),
              exception.getMessage(),
              Arrays.toString(exception.getStackTrace())),
          AuditContext.getChangeType(),
          AuditContext.getEventName()
      );
    } catch (Exception e) {
      logger.error("Error in createAuditLogService.logError: {}", e.getMessage());
    }
  }

}
