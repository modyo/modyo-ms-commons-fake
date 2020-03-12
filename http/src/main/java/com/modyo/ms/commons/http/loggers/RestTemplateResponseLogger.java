package com.modyo.ms.commons.http.loggers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.modyo.ms.commons.core.loggers.Logger;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
public class RestTemplateResponseLogger extends Logger {

  private Integer status;
  private HttpHeaders headers;
  private String body;
  private Date timeStampRequest;
  private Long timeTaken;

  @Override
  public void setBasicLogInformation() {
    this.setType("response");
    super.setBasicLogInformation();
    this.timeTaken = this.getTimeStamp().getTime() - timeStampRequest.getTime();
  }

}
