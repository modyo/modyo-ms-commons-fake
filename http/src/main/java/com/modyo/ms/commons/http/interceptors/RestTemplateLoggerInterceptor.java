package com.modyo.ms.commons.http.interceptors;

import com.modyo.ms.commons.http.loggers.RestTemplateRequestLogger;
import com.modyo.ms.commons.http.loggers.RestTemplateResponseLogger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class RestTemplateLoggerInterceptor implements ClientHttpRequestInterceptor {

  @Value("${spring.logger.sensitiverequestheaders}")
  private String sensitiveRequestHeaders;
  private Date tsRequest;

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    logRequest(request, body);
    ClientHttpResponse response = execution.execute(request, body);
    logResponse(response);
    return response;
  }

  private void logRequest(HttpRequest request, byte[] body) throws IOException {
    RestTemplateRequestLogger requestLog = RestTemplateRequestLogger.builder()
        .method(request.getMethodValue())
        .uri(request.getURI().toString())
        .headers(getRequestHeaders(request.getHeaders()))
        .body(new String(body, StandardCharsets.UTF_8))
        .build();
    requestLog.logInfo();
    tsRequest = requestLog.getTimeStamp();
  }

  private void logResponse(ClientHttpResponse response) throws IOException {
    StringBuilder inputStringBuilder = new StringBuilder();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8));
    String line = bufferedReader.readLine();
    while (line != null) {
      inputStringBuilder.append(line);
      inputStringBuilder.append('\n');
      line = bufferedReader.readLine();
    }
    RestTemplateResponseLogger.builder()
        .status(response.getStatusCode().value())
        .headers(response.getHeaders())
        .body(inputStringBuilder.toString())
        .timeStampRequest(tsRequest)
        .build()
        .logInfo();
  }


  private HttpHeaders getRequestHeaders(HttpHeaders requestHeaders) {
    HttpHeaders headers = new HttpHeaders();
    requestHeaders.forEach((name, values) -> headers.put(name, getValueRequestHeader(name, values)));
    return headers;
  }

  private List<String> getValueRequestHeader(String name, List<String> values) {
    return values.stream()
        .map(value -> Arrays.asList(sensitiveRequestHeaders.toLowerCase().split(",")).contains(name.toLowerCase())
            ? "*********"
            : value
        )
        .collect(Collectors.toList());
  }

}
