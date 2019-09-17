package com.modyo.services.dto;

import java.util.Date;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
@Getter
@Setter
public class LogDto extends Dto {

  private String level;
  private String type;
  private String correlationId;
  private Date timeStamp;

  public void logInfo() {
    this.level = "info";
    setBasicLogInformation();
    log.info(this.toJsonString());
  }

  public void logError() {
    this.level = "error";
    setBasicLogInformation();
    log.info(this.toJsonString());
  }

  public void setBasicLogInformation() {
    this.correlationId = Objects.requireNonNull(
      RequestContextHolder.currentRequestAttributes().getAttribute("correlationId", 0)
    ).toString();
    this.timeStamp = new Date(System.currentTimeMillis());
  }
}
