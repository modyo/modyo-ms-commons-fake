package com.modyo.ms.commons.http.loggers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.modyo.ms.commons.core.constants.LogTypes;
import com.modyo.ms.commons.core.loggers.CommonsLogger;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
@Getter
@Setter
public class RestTemplateRequestLogger extends CommonsLogger {

  @JsonIgnore
  private List<String> headersToObfuscate;

  private String method;
  private String uri;
  private HttpHeaders headers;
  private String body;

  public RestTemplateRequestLogger(
      String method,
      String uri,
      HttpHeaders headers,
      String body,
      List<String> headersToObfuscate) {
    super();
    this.headersToObfuscate = headersToObfuscate.stream()
        .map(String::toLowerCase)
        .collect(Collectors.toList());
    this.method = method;
    this.uri = uri;
    this.headers = getObfuscatedRequestHeaders(headers);
    this.body = body;
  }

  @Override
  public void setBasicLogInformation() {
    this.setType(LogTypes.REQUEST);
    super.setBasicLogInformation();
  }

  private HttpHeaders getObfuscatedRequestHeaders(HttpHeaders requestHeaders) {
    HttpHeaders obfuscatedHeaders = new HttpHeaders();
    requestHeaders.forEach((name, values) -> obfuscatedHeaders.put(
        name, getValueRequestHeader(name, values, headersToObfuscate)
    ));
    return obfuscatedHeaders;
  }

  private List<String> getValueRequestHeader(
      String name,
      List<String> values,
      List<String> headersNamesList) {
    return values.stream()
        .map(value -> headersNamesList.contains(name.toLowerCase())
            ? "*********"
            : value
        )
        .collect(Collectors.toList());
  }

}
