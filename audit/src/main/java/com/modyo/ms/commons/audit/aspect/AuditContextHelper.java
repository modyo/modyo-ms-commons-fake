package com.modyo.ms.commons.audit.aspect;

import com.modyo.ms.commons.audit.AuditLogType;
import com.modyo.ms.commons.audit.aspect.AuditAspect.ErrorMessageDto;
import com.modyo.ms.commons.audit.aspect.context.AuditGetContext;
import com.modyo.ms.commons.audit.service.CreateAuditLogService;
import java.util.Arrays;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AuditContextHelper {

  private static final Logger logger = LoggerFactory.getLogger(AuditContextHelper.class);

  private AuditContextHelper() {

  }

  public static void logSuccess(String prefix, CreateAuditLogService createAuditLogService, Object initValue, Object newValue) {
    log(AuditLogType.SUCCESS, prefix, createAuditLogService,
        initValue, newValue,
        AuditGetContext.getChangeType(prefix), AuditGetContext.getEventName(prefix)
    );
  }

  public static void logInfo(String prefix, CreateAuditLogService createAuditLogService, Object initValue, Object newValue,
      String changeType, String eventName) {

    log(AuditLogType.INFO, prefix, createAuditLogService,
        initValue, newValue,
        changeType, eventName
    );
  }

  public static void logError(String prefix, CreateAuditLogService createAuditLogService, Exception exception) {
    log(AuditLogType.ERROR, prefix, createAuditLogService,
        AuditGetContext.getInitialValue(prefix),
        new ErrorMessageDto(exception.getClass().getName(), exception.getMessage(), Arrays.toString(exception.getStackTrace())),
        AuditGetContext.getChangeType(prefix), AuditGetContext.getEventName(prefix)
        );
  }

  private static void log(AuditLogType auditLogType, String prefix, CreateAuditLogService createAuditLogService,
      Object initValue, Object newValue, String changeType, String eventName) {

    try {
      Optional.ofNullable(AuditGetContext.getParentEntityId()).ifPresent(parentEntityId ->
          createAuditLogService.log(
              auditLogType,
              AuditGetContext.getChildEntityId(prefix),
              parentEntityId,
              AuditGetContext.getParentEntity(),
              initValue,
              newValue,
              changeType,
              eventName
          )
      );
    } catch (Exception e) {
      logger.error("Error in createAuditLogService.log: {}", e.getMessage());
    }
  }

}
