package com.modyo.services.filters;

import com.modyo.services.filters.dto.RequestLogDto;
import com.modyo.services.filters.dto.ResponseLogDto;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Filtro para Transacciones HTTP
 */
@Component
@Order(2)
public class TransactionFilter implements Filter {

  private static final String[] SENSITIVE_REQUEST_HEADERS = {"authorization"};
  private HttpServletRequest request;
  private HttpServletResponse response;
  private String requestId;
  private Date tsRequest;
  @Value("${spring.application.name}")
  private String applicationName;

  @Override
  public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
    throws IOException, ServletException {
    this.request = (HttpServletRequest) request;
    this.response = (HttpServletResponse) response;
    generateRequestId();
    logRequest();
    addResponseHeaders();
    chain.doFilter(request, response);
    logResponse();
  }

  private void generateRequestId() {
    requestId = UUID.randomUUID().toString();
    RequestContextHolder.currentRequestAttributes().setAttribute("correlationId", requestId, 0);
  }

  private void addResponseHeaders() {
    response.addHeader("X-Correlation-ID", requestId);
    // TODO verificar si los dos headers de CORS se pueden eliminar.
    response.addHeader("Access-Control-Allow-Origin", "'*'");
    response.addHeader("Vary", "Origin");
    String fatherRequestId = request.getHeader("X-Parents-Correlation-Ids");
    if (fatherRequestId != null) {
      response.addHeader("X-Parents-Correlation-Ids", fatherRequestId);
    }
    if (applicationName != null) {
      response.addHeader("X-Application-Name", applicationName);
    }
  }

  private void logRequest() {
    RequestLogDto requestLog = RequestLogDto.builder()
      .method(request.getMethod())
      .uri(request.getRequestURI())
      .headers(getRequestHeaders())
      .parameters(getRequestParameters())
      .build();
    requestLog.logInfo();
    tsRequest = requestLog.getTimeStamp();
  }

  private void logResponse() {
    ResponseLogDto.builder()
      .status(response.getStatus())
      .headers(getResponseHeaders())
      .timeStampRequest(tsRequest)
      .build()
      .logInfo();
  }

  private Map<String, String> getRequestHeaders() {
    Map<String, String> map = new HashMap<>();
    Enumeration headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String name = (String) headerNames.nextElement();
      map.put(name, getValueRequestHeader(name));
    }
    return map;
  }

  private String getValueRequestHeader(String name) {
    if (Arrays.asList(SENSITIVE_REQUEST_HEADERS).contains(name)) {
      return "*********";
    }
    return request.getHeader(name);
  }

  private Map<String, String> getRequestParameters() {
    Map<String, String> map = new HashMap<>();
    Enumeration parameterNames = request.getParameterNames();
    while (parameterNames.hasMoreElements()) {
      String key = (String) parameterNames.nextElement();
      map.put(key, request.getParameter(key));
    }
    return map;
  }

  private Map<String, String> getResponseHeaders() {
    Map<String, String> map = new HashMap<>();
    Collection<String> headerNames = response.getHeaderNames();
    headerNames.forEach(name -> map.put(name, response.getHeader(name)));
    return map;
  }
}
