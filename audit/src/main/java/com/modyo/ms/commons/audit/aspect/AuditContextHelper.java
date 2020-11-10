package com.modyo.ms.commons.audit.aspect;

import com.modyo.ms.commons.audit.AuditLogType;
import com.modyo.ms.commons.audit.aspect.AuditAspect.ErrorMessageDto;
import com.modyo.ms.commons.audit.service.ChangeType;
import com.modyo.ms.commons.audit.service.CreateAuditLogService;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AuditContextHelper {

  private static final Logger logger = LoggerFactory.getLogger(AuditContextHelper.class);

  private AuditContextHelper() {

  }

  public static void logSuccess(CreateAuditLogService createAuditLogService, Object initValue, Object newValue) {
    log(AuditLogType.SUCCESS, createAuditLogService,
        initValue, newValue,
        AuditContext.getChangeType(), AuditContext.getEventName()
    );
  }

  public static void logInfo(CreateAuditLogService createAuditLogService, Object initValue, Object newValue) {

    log(AuditLogType.INFO, createAuditLogService,
        initValue, newValue,
        AuditContext.getChangeType(), AuditContext.getEventName()
    );
  }

  public static void logError(CreateAuditLogService createAuditLogService, Exception exception) {
    log(AuditLogType.ERROR, createAuditLogService,
        AuditContext.getInitialValue(),
        new ErrorMessageDto(exception.getClass().getName(), exception.getMessage(), Arrays.toString(exception.getStackTrace())),
        AuditContext.getChangeType(), AuditContext.getEventName()
        );
  }

  private static void log(AuditLogType auditLogType, CreateAuditLogService createAuditLogService,
      Object initValue, Object newValue, ChangeType changeType, String eventName) {
    try {
      createAuditLogService.log(
          auditLogType,
          AuditContext.getChildEntityId(),
          AuditContext.getParentEntityId(),
          AuditContext.getParentEntity(),
          initValue,
          newValue,
          changeType,
          eventName
      );
    } catch (Exception e) {
      logger.error("Error in createAuditLogService.log: {}", e.getMessage());
    }
  }

}
