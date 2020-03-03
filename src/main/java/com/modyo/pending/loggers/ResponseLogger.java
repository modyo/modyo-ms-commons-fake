package com.modyo.pending.loggers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
public class ResponseLogger extends Logger {

  @JsonProperty("type")
  private static final String TYPE = "response";
  private Integer status;
  private Map<String, String> headers;
  private Date timeStampRequest;
  private Long timeTaken;

  @Override
  public void setBasicLogInformation() {
    this.setType("response");
    super.setBasicLogInformation();
    this.timeTaken = this.getTimeStamp().getTime() - timeStampRequest.getTime();
  }

}
