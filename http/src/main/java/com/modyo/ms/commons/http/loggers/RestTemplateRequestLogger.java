package com.modyo.ms.commons.http.loggers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.modyo.ms.commons.core.loggers.Logger;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
public class RestTemplateRequestLogger extends Logger {

  private String method;
  private String uri;
  private HttpHeaders headers;
  private String body;

  @Override
  public void setBasicLogInformation() {
    this.setType("request");
    super.setBasicLogInformation();
  }

}
