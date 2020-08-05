package com.modyo.ms.commons.http.filters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.modyo.ms.commons.http.config.properties.LoggingFilterProperties;
import com.modyo.ms.commons.http.loggers.RequestLogger;
import com.modyo.ms.commons.http.loggers.ResponseLogger;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(2)
public class LoggingFilter implements Filter {

  @JsonIgnore
  private final LoggingFilterProperties loggingFilterProperties;

  @Override
  public void doFilter(
      ServletRequest request,
      ServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    if (loggingFilterProperties.isEnabled()) {
      Date tsRequest = new Date(System.currentTimeMillis());
      logRequest((HttpServletRequest) request);
      chain.doFilter(request, response);
      logResponse((HttpServletResponse) response, tsRequest);
    } else {
      chain.doFilter(request, response);
    }
  }

  private void logRequest(HttpServletRequest request) {
    new RequestLogger(
        request,
        loggingFilterProperties
            .getObfuscate()
            .getRequest()
            .getHeaders()).logInfo();
  }

  private void logResponse(HttpServletResponse response, Date tsRequest) {
    new ResponseLogger(
        response.getStatus(),
        getResponseHeaders(response),
        tsRequest).logInfo();
  }

  private Map<String, String> getResponseHeaders(HttpServletResponse response) {
    Map<String, String> responseHeaders = new HashMap<>();
    Collection<String> headerNames = response.getHeaderNames();
    headerNames.forEach(name -> responseHeaders.put(name, response.getHeader(name)));
    return responseHeaders;
  }

}
