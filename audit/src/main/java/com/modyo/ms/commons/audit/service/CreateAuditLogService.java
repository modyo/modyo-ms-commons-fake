package com.modyo.ms.commons.audit.service;


import com.modyo.ms.commons.audit.AuditLogType;

public interface CreateAuditLogService {

  void log(AuditLogType logType, String auditableId, String auditableParentId, Object parentValue, Object initialValue, Object newValue,
      String changeType, String event);


}
