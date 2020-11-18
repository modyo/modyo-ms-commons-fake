package com.modyo.ms.commons.audit.aspect;

import org.springframework.web.context.request.RequestContextHolder;

public class AuditContext {

  private static final String PARENT_ENTITY = "audit_parent_entity";
  private static final String PARENT_ENTITY_ID = "audit_parent_entity_id";
  private static final String INITIAL_VALUE = "audit_initial_value";
  private static final String NEW_VALUE = "audit_new_value";
  private static final String ENTITY_ID = "audit_entity_id";
  private static final String CHANGE_TYPE = "audit_change_type";
  private static final String EVENT_NAME = "audit_event_name";

  private static final Integer AUDIT_SCOPE = 0;

  private AuditContext() {

  }

  public static void setInitialInfo(Object parentEntity, String parentEntityId, Object entity, String entityId) {
    setParentEntity(parentEntity, parentEntityId);
    setInitialInfo(entity, entityId);
  }

  private static void setParentEntity(Object parentEntity, String parentEntityId) {
    RequestContextHolder.currentRequestAttributes().setAttribute(PARENT_ENTITY, parentEntity, AUDIT_SCOPE);
    RequestContextHolder.currentRequestAttributes().setAttribute(PARENT_ENTITY_ID, parentEntityId, AUDIT_SCOPE);
  }

  public static void setInitialInfo(Object entity, String entityId) {
    RequestContextHolder.currentRequestAttributes().setAttribute(INITIAL_VALUE, entity, AUDIT_SCOPE);
    RequestContextHolder.currentRequestAttributes().setAttribute(ENTITY_ID, entityId, AUDIT_SCOPE);
  }

  public static void setNewValue(Object value) {
    RequestContextHolder.currentRequestAttributes().setAttribute(NEW_VALUE, value, AUDIT_SCOPE);
  }

  public static void setEventInfo(String changeType, String eventName) {
    RequestContextHolder.currentRequestAttributes().setAttribute(CHANGE_TYPE, changeType, AUDIT_SCOPE);
    RequestContextHolder.currentRequestAttributes().setAttribute(EVENT_NAME, eventName, AUDIT_SCOPE);
  }

  static String getChangeType() {
    return (String) RequestContextHolder.currentRequestAttributes().getAttribute(CHANGE_TYPE, AUDIT_SCOPE);
  }

  static String getEventName() {
    return (String) RequestContextHolder.currentRequestAttributes().getAttribute(EVENT_NAME, AUDIT_SCOPE);
  }

  static Object getParentEntity() {
    return RequestContextHolder.currentRequestAttributes().getAttribute(PARENT_ENTITY, AUDIT_SCOPE);
  }

  static Object getInitialValue() {
    return RequestContextHolder.currentRequestAttributes().getAttribute(INITIAL_VALUE, AUDIT_SCOPE);
  }

  static Object getNewValue() {
    return RequestContextHolder.currentRequestAttributes().getAttribute(NEW_VALUE, AUDIT_SCOPE);
  }

  static String getParentEntityId() {
    return (String) RequestContextHolder.currentRequestAttributes().getAttribute(PARENT_ENTITY_ID, AUDIT_SCOPE);
  }

  static String getChildEntityId() {
    return (String) RequestContextHolder.currentRequestAttributes().getAttribute(ENTITY_ID, AUDIT_SCOPE);
  }

}
