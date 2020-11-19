package com.modyo.ms.commons.audit.aspect.context;

import static com.modyo.ms.commons.audit.aspect.context.AuditContext.CHANGE_TYPE;
import static com.modyo.ms.commons.audit.aspect.context.AuditContext.ENTITY_ID;
import static com.modyo.ms.commons.audit.aspect.context.AuditContext.EVENT_NAME;
import static com.modyo.ms.commons.audit.aspect.context.AuditContext.INITIAL_VALUE;
import static com.modyo.ms.commons.audit.aspect.context.AuditContext.NEW_VALUE;
import static com.modyo.ms.commons.audit.aspect.context.AuditContext.PARENT_ENTITY;
import static com.modyo.ms.commons.audit.aspect.context.AuditContext.PARENT_ENTITY_ID;
import static com.modyo.ms.commons.audit.aspect.context.AuditContext.getAttribute;

public class AuditGetContext {

  private AuditGetContext() {

  }

  public static String getChangeType(String prefix) {
    return (String) getAttribute(prefix, CHANGE_TYPE);
  }

  public static String getEventName(String prefix) {
    return (String) getAttribute(prefix, EVENT_NAME);
  }

  public static Object getParentEntity() {
    return getAttribute(null, PARENT_ENTITY);
  }

  public static Object getInitialValue(String prefix) {
    return getAttribute(prefix, INITIAL_VALUE);
  }

  public static Object getNewValue(String prefix) {
    return getAttribute(prefix, NEW_VALUE);
  }

  public static String getParentEntityId() {
    return (String) getAttribute(null, PARENT_ENTITY_ID);
  }

  public static String getChildEntityId(String prefix) {
    return (String) getAttribute(prefix, ENTITY_ID);
  }

}
