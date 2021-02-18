package com.modyo.ms.commons.http.interceptors;

import com.modyo.ms.commons.http.config.properties.RestTemplateLoggerProperties;
import com.modyo.ms.commons.http.loggers.RestTemplateRequestLogger;
import com.modyo.ms.commons.http.loggers.RestTemplateResponseLogger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestTemplateLoggerInterceptor implements ClientHttpRequestInterceptor {

  private final RestTemplateLoggerProperties restTemplateLoggerProperties;
  private final Optional<RestTemplateInterceptorService> restTemplateInterceptorService;

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request,
      byte[] body,
      ClientHttpRequestExecution execution) throws IOException {
    Date tsRequest = new Date(System.currentTimeMillis());
    RestTemplateRequestLogger restTemplateRequestLogger = logRequest(request, body);
    ClientHttpResponse response = execution.execute(request, body);
    RestTemplateResponseLogger restTemplateResponseLogger = logResponse(response, tsRequest, request.getHeaders());
    restTemplateInterceptorService.ifPresent(service ->
        service.intercept(restTemplateRequestLogger, restTemplateResponseLogger));
    return response;
  }

  private RestTemplateRequestLogger logRequest(HttpRequest request, byte[] body) {
    RestTemplateRequestLogger restTemplateRequestLogger = new RestTemplateRequestLogger(
        request.getMethodValue(),
        request.getURI().toString(),
        request.getHeaders(),
        new String(body, StandardCharsets.UTF_8),
        restTemplateLoggerProperties.getObfuscate().getRequest().getHeaders()
    );
    restTemplateRequestLogger.logInfo();
    return restTemplateRequestLogger;
  }

  private RestTemplateResponseLogger logResponse(ClientHttpResponse response, Date tsRequest, HttpHeaders requestHeaders) throws IOException {
    RestTemplateResponseLogger restTemplateResponseLogger = new RestTemplateResponseLogger(
        response.getStatusCode().value(),
        response.getHeaders(),
        requestHeaders,
        responseBodyString(response),
        tsRequest);
    restTemplateResponseLogger.logInfo();
    return restTemplateResponseLogger;
  }

  private String responseBodyString(ClientHttpResponse response) {
    try {
      StringBuilder inputStringBuilder = new StringBuilder();
      BufferedReader bufferedReader = new BufferedReader(
          new InputStreamReader(response.getBody(),StandardCharsets.UTF_8));
      String line = bufferedReader.readLine();
      while (line != null) {
        inputStringBuilder.append(line);
        inputStringBuilder.append('\n');
        line = bufferedReader.readLine();
      }
      return inputStringBuilder.toString();
    } catch (IOException e) {
      return "";
    }
  }
}
