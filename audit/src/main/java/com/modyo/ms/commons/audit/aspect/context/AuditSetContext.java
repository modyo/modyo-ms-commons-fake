package com.modyo.ms.commons.audit.aspect.context;

import static com.modyo.ms.commons.audit.aspect.context.AuditContext.CHANGE_TYPE;
import static com.modyo.ms.commons.audit.aspect.context.AuditContext.ENTITY_ID;
import static com.modyo.ms.commons.audit.aspect.context.AuditContext.EVENT_NAME;
import static com.modyo.ms.commons.audit.aspect.context.AuditContext.HTTP_REQUEST_CHANGE_TYPE;
import static com.modyo.ms.commons.audit.aspect.context.AuditContext.HTTP_REQUEST_EVENT;
import static com.modyo.ms.commons.audit.aspect.context.AuditContext.INITIAL_VALUE;
import static com.modyo.ms.commons.audit.aspect.context.AuditContext.NEW_VALUE;
import static com.modyo.ms.commons.audit.aspect.context.AuditContext.PARENT_ENTITY;
import static com.modyo.ms.commons.audit.aspect.context.AuditContext.PARENT_ENTITY_ID;
import static com.modyo.ms.commons.audit.aspect.context.AuditContext.getAttribute;
import static com.modyo.ms.commons.audit.aspect.context.AuditContext.setAttribute;

public class AuditSetContext {

  private AuditSetContext() {

  }

  public static void setParentEntityAndInitialInfo(String prefix, Object parentEntity, String parentEntityId, Object entity, String entityId) {
    setParentEntity(parentEntity, parentEntityId);
    setInitialEntityInfo(prefix, entity, entityId);
  }

  public static void setParentEntity(Object parentEntity, String parentEntityId) {
    setAttribute(null, PARENT_ENTITY, parentEntity);
    setAttribute(null, PARENT_ENTITY_ID, parentEntityId);
  }

  public static void setInitialEntityInfo(String prefix, Object entity, String entityId) {
    setAttribute(prefix, INITIAL_VALUE, entity);
    setAttribute(prefix, ENTITY_ID, entityId);
  }

  public static void setNewValue(String prefix, Object value) {
    setAttribute(prefix, NEW_VALUE, value);
  }

  public static void setEventInfo(String prefix, String changeType, String eventName) {
    setAttribute(prefix, CHANGE_TYPE, changeType);
    setAttribute(prefix, EVENT_NAME, eventName);
  }

  public static void setHttpEventInfo(String eventName) {
    setAttribute(null, HTTP_REQUEST_EVENT, eventName);
  }

  public static String resetHttpEventInfo() {
    String eventName = (String) getAttribute(null, HTTP_REQUEST_EVENT);
    setAttribute(null, HTTP_REQUEST_EVENT, null);
    return eventName;
  }

  public static void setHttpChangeType(String changeType) {
    setAttribute(null, HTTP_REQUEST_CHANGE_TYPE, changeType);
  }

  public static String resetHttpChangeType() {
    String eventName = (String) getAttribute(null, HTTP_REQUEST_CHANGE_TYPE);
    setAttribute(null, HTTP_REQUEST_CHANGE_TYPE, null);
    return eventName;
  }

}
