package com.modyo.ms.commons.core.utils;

import java.util.UUID;
import org.springframework.web.context.request.RequestContextHolder;

public class CorrelationIdUtils {

  private CorrelationIdUtils() {
  }

  public static final String CONTEXT_KEY = "correlationId";

  public static void generateCorrelationId() {
    String correlationId = UUID.randomUUID().toString();
    RequestContextHolder.currentRequestAttributes().setAttribute(CONTEXT_KEY, correlationId, 0);
  }

}
