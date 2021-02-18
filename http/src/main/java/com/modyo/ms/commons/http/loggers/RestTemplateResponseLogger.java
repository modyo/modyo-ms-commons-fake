package com.modyo.ms.commons.http.loggers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.modyo.ms.commons.core.constants.LogTypes;
import com.modyo.ms.commons.core.loggers.CommonsLogger;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
@Getter
@Setter
public class RestTemplateResponseLogger extends CommonsLogger {

  private static final String BODY_PARAMS_OBFUSCATE_HEADER_KEY = "ObfuscateResponseBodyParams";

  private final Integer status;
  private final HttpHeaders headers;
  private final String body;
  private final Date timeStampRequest;
  private Long timeTaken;

  public RestTemplateResponseLogger(
      Integer status,
      HttpHeaders httpHeaders,
      HttpHeaders requestHeaders,
      String body,
      Date timeStampRequest
  ) {
    this.status = status;
    this.headers = httpHeaders;
    this.timeStampRequest = timeStampRequest;
    List<String> bodyParamsToObfuscate = Optional.ofNullable(requestHeaders.get(BODY_PARAMS_OBFUSCATE_HEADER_KEY)).orElse(Collections.emptyList());
    this.body = ObfuscateBodyParamsService.obfuscate(body, bodyParamsToObfuscate);
  }

  @Override
  public void setBasicLogInformation() {
    this.setType(LogTypes.RESPONSE);
    super.setBasicLogInformation();
    this.timeTaken = new Date(System.currentTimeMillis()).getTime() - timeStampRequest.getTime();
  }

}
