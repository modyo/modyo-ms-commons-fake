package com.modyo.ms.commons.core.utils;

import com.modyo.ms.commons.core.loggers.CommonsLogger;
import java.util.UUID;
import org.springframework.web.context.request.RequestContextHolder;

public class CorrelationIdUtils {

  private CorrelationIdUtils() {
  }

  public static void generateCorrelationId() {
    String correlationId = UUID.randomUUID().toString();
    RequestContextHolder.currentRequestAttributes().setAttribute(CommonsLogger.CORRELATION_ID_KEY, correlationId, 0);
  }

}
