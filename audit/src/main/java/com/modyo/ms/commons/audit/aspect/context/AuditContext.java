package com.modyo.ms.commons.audit.aspect.context;

import java.util.Optional;
import org.springframework.web.context.request.RequestContextHolder;

public class AuditContext {

  static final String PARENT_ENTITY = "audit_parent_entity";
  static final String PARENT_ENTITY_ID = "audit_parent_entity_id";
  static final String INITIAL_VALUE = "audit_initial_value";
  static final String NEW_VALUE = "audit_new_value";
  static final String ENTITY_ID = "audit_entity_id";
  static final String CHANGE_TYPE = "audit_change_type";
  static final String EVENT_NAME = "audit_event_name";

  private static final Integer AUDIT_SCOPE = 0;
  public static final String CURRENT_PREFIX = "current";

  private AuditContext() {

  }

  static void setAttribute(String prefix, String attributeKey, Object value) {
    RequestContextHolder.currentRequestAttributes().setAttribute(
        buildCompositeKey(prefix, attributeKey),
        value,
        AUDIT_SCOPE);
    RequestContextHolder.currentRequestAttributes().setAttribute(
        buildCompositeKey(CURRENT_PREFIX, attributeKey),
        value,
        AUDIT_SCOPE);
  }

  static Object getAttribute(String prefix, String attributeKey) {
    return RequestContextHolder.currentRequestAttributes().getAttribute(
        buildCompositeKey(prefix, attributeKey),
        AUDIT_SCOPE);
  }

  private static String buildCompositeKey(String prefix, String key) {
    return Optional.ofNullable(prefix)
        .filter(s -> !s.isEmpty())
        .map(s -> s.concat("_"))
        .orElse("")
        .concat(key);
  }

}
