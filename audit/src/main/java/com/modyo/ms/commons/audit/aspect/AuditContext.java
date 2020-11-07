package com.modyo.ms.commons.audit.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.context.request.RequestContextHolder;

public class AuditContext {

  private static final String PARENT_ENTITY = "audit_parent_entity";
  private static final String PARENT_ENTITY_ID = "audit_parent_entity_id";
  private static final String ENTITY = "audit_entity";
  private static final String ENTITY_ID = "audit_entity_id";

  private static final Integer AUDIT_SCOPE = 0;

  public static void setDomainEntity(Object parentEntity, String parentEntityId, Object entity, String entityId) {
    setParentEntity(parentEntity, parentEntityId);
    setDomainEntity(entity, entityId);
  }

  public static void setDomainEntity(Object entity, String entityId) {
    RequestContextHolder.currentRequestAttributes().setAttribute(ENTITY, entity, AUDIT_SCOPE);
    RequestContextHolder.currentRequestAttributes().setAttribute(ENTITY_ID, entityId, AUDIT_SCOPE);
  }

  public static Object getParentEntity() {
    return getJson(PARENT_ENTITY);
  }

  public static String getParentEntityId() {
    return (String) RequestContextHolder.currentRequestAttributes().getAttribute(PARENT_ENTITY_ID, AUDIT_SCOPE);
  }

  private static void setParentEntity(Object parentEntity, String parentEntityId) {
    RequestContextHolder.currentRequestAttributes().setAttribute(PARENT_ENTITY, parentEntity, AUDIT_SCOPE);
    RequestContextHolder.currentRequestAttributes().setAttribute(PARENT_ENTITY_ID, parentEntityId, AUDIT_SCOPE);
  }

  private static String getJson(String key) {
    try {
      Object object = RequestContextHolder.currentRequestAttributes().getAttribute(key, AUDIT_SCOPE);
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(object);
    } catch (Exception e) {
      return "";
    }
  }

  private static String getString(String key) {
    try {
      return (String) RequestContextHolder.currentRequestAttributes().getAttribute(key, AUDIT_SCOPE);
    } catch (Exception e) {
      return "";
    }
  }

}
