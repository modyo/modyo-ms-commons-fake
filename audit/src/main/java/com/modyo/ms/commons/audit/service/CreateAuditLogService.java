package com.modyo.ms.commons.audit.service;


public interface CreateAuditLogService {

  void logInfo(String auditableId, Object initialValue, Object newValue, ChangeType changeType, String event);

  void logInfo(String auditableId, String auditableParentId, Object parentValue, Object initialValue, Object newValue,
      ChangeType changeType, String event);

  void logError(String auditableId, Object initialValue, Object newValue, ChangeType changeType, String event);

  void logError(String auditableId, String auditableParentId, Object parentValue, Object initialValue, Object newValue,
      ChangeType changeType, String event);


}
