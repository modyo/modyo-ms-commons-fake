package com.modyo.ms.commons.http.loggers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.modyo.ms.commons.core.constants.LogTypes;
import com.modyo.ms.commons.core.loggers.Logger;
import java.util.Date;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
@Getter
@Setter
public class ResponseLogger extends Logger {

  private final Integer status;
  private final Map<String, String> headers;
  private final Date timeStampRequest;
  private Long timeTaken;

  @Override
  public void setBasicLogInformation() {
    this.setType(LogTypes.RESPONSE);
    super.setBasicLogInformation();
    this.timeTaken = this.getTimeStamp().getTime() - timeStampRequest.getTime();
  }

}
