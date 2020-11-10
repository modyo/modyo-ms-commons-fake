package com.modyo.ms.commons.audit.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modyo.ms.commons.audit.AuditLogType;
import com.modyo.ms.commons.audit.service.ChangeType;
import java.util.Optional;

class AuditJpaEntityFactory {

  private AuditJpaEntityFactory() {

  }

  public static AuditJpaEntity create(
      AuditLogType logLevel, String auditableId, String auditableParentId,
      Object parentValue, Object initialValue, Object newValue,
      String createdBy, String userAgent,
      ChangeType changeType, String event) {
    AuditJpaEntity entity = new AuditJpaEntity();
    String parentId = Optional.ofNullable(auditableParentId).orElse(auditableId);
    Object calculatedParentValue = Optional.ofNullable(parentValue).orElse(initialValue);

    entity.setAuditableId(auditableId);
    entity.setAuditableType(getClassName(initialValue));
    entity.setAuditableParentId(parentId);
    entity.setAuditableParentType(getClassName(calculatedParentValue));
    entity.setInitialValue(toJson(initialValue));
    entity.setNewValue(toJson(newValue));
    entity.setCreatedBy(createdBy);
    entity.setUserAgent(userAgent);
    entity.setChangeType(changeType);
    entity.setEvent(event);
    entity.setLogType(logLevel);

    return entity;
  }

  private static String getClassName(Object entity) {
    return entity.getClass().getSimpleName();
  }

  private static String toJson(Object entity) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(entity);
    } catch (JsonProcessingException e) {
      return "";
    }
  }




}
