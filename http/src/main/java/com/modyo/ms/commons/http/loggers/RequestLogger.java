package com.modyo.ms.commons.http.loggers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.modyo.ms.commons.core.loggers.Logger;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
public class RequestLogger extends Logger {

  private String method;
  private String uri;
  private Map<String, String> headers;
  private Map<String, String> parameters;

  @Override
  public void setBasicLogInformation() {
    this.setType("request");
    super.setBasicLogInformation();
  }

}
