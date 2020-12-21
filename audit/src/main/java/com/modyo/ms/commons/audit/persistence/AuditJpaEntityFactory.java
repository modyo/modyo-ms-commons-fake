package com.modyo.ms.commons.audit.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.modyo.ms.commons.audit.AuditLogType;
import java.util.Optional;

class AuditJpaEntityFactory {

  private AuditJpaEntityFactory() {

  }

  public static AuditJpaEntity create(
      AuditLogType logLevel, String auditableId, String auditableParentId,
      Object parentValue, Object initialValue, Object newValue,
      String createdBy, String userAgent,
      String changeType, String event) {
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
    return Optional.ofNullable(entity)
        .filter(e -> !(e instanceof String))
        .map(Object::getClass)
        .map(Class::getSimpleName)
        .orElse(null);
  }

  private static String toJson(Object entity) {
    if (entity == null) {
      return null;
    } else if (entity instanceof String) {
      return entity.toString();
    }
    try {
      ObjectMapper objectMapper = new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      return objectMapper.writeValueAsString(entity);
    } catch (JsonProcessingException e) {
      return "";
    }
  }




}
