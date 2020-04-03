package com.modyo.ms.commons.http.loggers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.modyo.ms.commons.core.constants.LogTypes;
import com.modyo.ms.commons.core.loggers.CommonsLogger;
import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
@RequiredArgsConstructor
@Getter
@Setter
public class RestTemplateResponseLogger extends CommonsLogger {

  private final Integer status;
  private final HttpHeaders headers;
  private final String body;
  private final Date timeStampRequest;
  private Long timeTaken;

  @Override
  public void setBasicLogInformation() {
    this.setType(LogTypes.RESPONSE);
    super.setBasicLogInformation();
    this.timeTaken = new Date(System.currentTimeMillis()).getTime() - timeStampRequest.getTime();
  }

}
