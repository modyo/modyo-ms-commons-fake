package com.modyo.ms.commons.audit.persistence;

import com.modyo.ms.commons.audit.AuditLogType;
import com.modyo.ms.commons.audit.service.ChangeType;
import com.modyo.ms.commons.audit.service.CreateAuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class CreateAuditLogServiceAdapter implements CreateAuditLogService {

  private final AuditJpaRepository auditJpaRepository;

  @Override
  public void logInfo(String auditableId, String auditableParentId, Object parentValue, Object initialValue,
      Object newValue, ChangeType changeType, String event) {
    log(AuditLogType.INFO, auditableId, auditableParentId,parentValue,
        initialValue,newValue,changeType,event);

  }

  @Override
  public void logSuccess(String auditableId, String auditableParentId, Object parentValue, Object initialValue,
      Object newValue, ChangeType changeType, String event) {
    log(AuditLogType.SUCCESS, auditableId, auditableParentId,parentValue,
        initialValue,newValue,changeType,event);
  }

  @Override
  public void logError(String auditableId, String auditableParentId, Object parentValue, Object initialValue,
      Object newValue, ChangeType changeType, String event) {
    log(AuditLogType.ERROR, auditableId, auditableParentId,parentValue,
        initialValue,newValue,changeType,event);
  }

  private void log(AuditLogType logLevel, String auditableId, String auditableParentId, Object parentValue, Object initialValue,
      Object newValue, ChangeType changeType, String event) {
    AuditJpaEntity auditJpaEntity = AuditJpaEntityFactory.create(
        logLevel,
        auditableId,
        auditableParentId,
        parentValue,
        initialValue,
        newValue,
        changeType,
        event);

    auditJpaRepository.save(auditJpaEntity);
  }
}
