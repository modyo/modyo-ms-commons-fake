package com.modyo.ms.commons.http.loggers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.modyo.ms.commons.core.constants.LogTypes;
import com.modyo.ms.commons.core.loggers.Logger;
import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
@Getter
@Setter
public class RestTemplateResponseLogger extends Logger {

  private final Integer status;
  private final HttpHeaders headers;
  private final String body;
  private final Date timeStampRequest;
  private Long timeTaken;



  @Override
  public void setBasicLogInformation() {
    this.setType(LogTypes.RESPONSE);
    super.setBasicLogInformation();
    this.timeTaken = this.getTimeStamp().getTime() - timeStampRequest.getTime();
  }

}
