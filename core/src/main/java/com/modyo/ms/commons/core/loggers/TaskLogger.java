package com.modyo.ms.commons.core.loggers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@JsonInclude(Include.NON_NULL)
@Slf4j
@Getter
public class TaskLogger extends CommonsLogger {

  private String taskName;
  private String errorMessage;

  private TaskLogger(String taskName) {
    this.taskName = taskName;
    this.setBasicLogInformation();
  }

  public static TaskLogger start(String taskName) {
    TaskLogger logger = new TaskLogger(taskName);
    logger.setType("task_start");
    return logger;
  }

  public static TaskLogger end(String taskName) {
    TaskLogger logger = new TaskLogger(taskName);
    logger.setType("task_end");
    return logger;
  }

  public static TaskLogger error(String taskName, String errorMessage) {
    TaskLogger logger = new TaskLogger(taskName);
    logger.errorMessage = errorMessage;
    logger.setType("task_error");
    return logger;
  }

}
