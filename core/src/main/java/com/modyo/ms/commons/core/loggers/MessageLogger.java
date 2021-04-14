package com.modyo.ms.commons.core.loggers;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
@Getter
@Setter
public class MessageLogger extends CommonsLogger {

  private String exceptionMessage;

  private MessageLogger(String message) {
    super();
    super.setBasicLogInformation();
    super.setMessage(message);
  }

  public static void logInfo(String message) {
    MessageLogger messageLogger = new MessageLogger(message);
    messageLogger.logInfo();
  }

  public static void logError(String message) {
    MessageLogger messageLogger = new MessageLogger(message);
    messageLogger.logError();
  }

  public static void logError(String message, Exception e) {
    MessageLogger messageLogger = new MessageLogger(message);
    messageLogger.exceptionMessage = e.getMessage();
    messageLogger.logError();
  }
}
