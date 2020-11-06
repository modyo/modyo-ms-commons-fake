package com.modyo.ms.commons.audit.persistence;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modyo.ms.commons.audit.service.ChangeType;
import java.util.Optional;
import org.springframework.boot.logging.LogLevel;

class AuditJpaEntityFactory {

  private AuditJpaEntityFactory() {

  }

  public static AuditJpaEntity create(
      LogLevel logLevel, String auditableId, String auditableParentId,
      Object parentValue, Object initialValue, Object newValue,
      ChangeType changeType, String event) {
    AuditJpaEntity entity = new AuditJpaEntity();
    if (!isSameType(initialValue, newValue)) {
      throw new IllegalArgumentException("initValue and newValue must be of the same type");
    }
    String parentId = Optional.ofNullable(auditableParentId).orElse(auditableId);
    Object calculatedParentValue = Optional.ofNullable(parentValue).orElse(initialValue);

    entity.setAuditableId(auditableId);
    entity.setAuditableType(getClassName(initialValue));
    entity.setAuditableParentId(parentId);
    entity.setAuditableParentType(getClassName(calculatedParentValue));
    entity.setInitialValue(toJson(initialValue));
    entity.setNewValue(toJson(newValue));
    entity.setChangeType(changeType);
    entity.setEvent(event);
    entity.setLogLevel(logLevel);

    return entity;
  }

  private static String getClassName(Object entity) {
    return entity.getClass().getSimpleName();
  }

  private static String toJson(Object entity) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
      return objectMapper.writeValueAsString(entity);
    } catch (Exception e) {
      return "";
    }
  }

  private static boolean isSameType(Object obj1, Object obj2) {
    return (obj1 == obj2 ||
        (obj1 != null
            && obj2 != null
            && obj1.getClass() == obj2.getClass())
    );
  }




}
