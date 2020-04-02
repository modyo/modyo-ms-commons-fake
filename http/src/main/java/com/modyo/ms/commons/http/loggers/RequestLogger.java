package com.modyo.ms.commons.http.loggers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.modyo.ms.commons.core.constants.LogTypes;
import com.modyo.ms.commons.core.loggers.Logger;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class RequestLogger extends Logger {

  @JsonIgnore
  private List<String> headersToObfuscate;

  private String method;
  private String uri;
  private Map<String, String> headers;
  private Map<String, String> parameters;

  public RequestLogger(HttpServletRequest request, List<String> headersToObfuscate) {
    this.headersToObfuscate = headersToObfuscate.stream()
        .map(String::toLowerCase)
        .collect(Collectors.toList());
    this.method = request.getMethod();
    this.uri = request.getRequestURI();
    this.headers = getObfuscatedRequestHeaders(request);
    this.parameters = getRequestParameters(request);
  }

  @Override
  public void setBasicLogInformation() {
    this.setType(LogTypes.REQUEST);
    super.setBasicLogInformation();
  }

  private Map<String, String> getObfuscatedRequestHeaders(HttpServletRequest request) {
    Map<String, String> obfuscatedHeaders = new HashMap<>();
    Enumeration headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String name = (String) headerNames.nextElement();
      obfuscatedHeaders.put(name, getValueRequestHeader(name, request));
    }
    return obfuscatedHeaders;
  }

  private String getValueRequestHeader(
      String name,
      HttpServletRequest request) {
    return headersToObfuscate.contains(name.toLowerCase())
        ? "*********"
        : request.getHeader(name);
  }

  private Map<String, String> getRequestParameters(HttpServletRequest request) {
    Map<String, String> requestParameters = new HashMap<>();
    Enumeration parameterNames = request.getParameterNames();
    while (parameterNames.hasMoreElements()) {
      String key = (String) parameterNames.nextElement();
      requestParameters.put(key, request.getParameter(key));
    }
    return requestParameters;
  }

}
