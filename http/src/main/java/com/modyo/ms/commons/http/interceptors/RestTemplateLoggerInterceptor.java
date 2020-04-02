package com.modyo.ms.commons.http.interceptors;

import com.modyo.ms.commons.http.config.RestTemplateLoggerProperties;
import com.modyo.ms.commons.http.loggers.RestTemplateRequestLogger;
import com.modyo.ms.commons.http.loggers.RestTemplateResponseLogger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestTemplateLoggerInterceptor implements ClientHttpRequestInterceptor {

  private final RestTemplateLoggerProperties restTemplateLoggerProperties;

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request,
      byte[] body,
      ClientHttpRequestExecution execution) throws IOException {
    Date tsRequest = logRequest(request, body);
    ClientHttpResponse response = execution.execute(request, body);
    logResponse(response, tsRequest);
    return response;
  }

  private Date logRequest(HttpRequest request, byte[] body) {
    RestTemplateRequestLogger requestLog = new RestTemplateRequestLogger(
        request.getMethodValue(),
        request.getURI().toString(),
        request.getHeaders(),
        new String(body, StandardCharsets.UTF_8),
        restTemplateLoggerProperties.getObfuscate().getRequest().getHeaders()
    );
    requestLog.logInfo();
    return requestLog.getTimeStamp();
  }

  private void logResponse(ClientHttpResponse response, Date tsRequest) throws IOException {
    StringBuilder inputStringBuilder = new StringBuilder();
    BufferedReader bufferedReader = new BufferedReader(
        new InputStreamReader(response.getBody(),StandardCharsets.UTF_8));
    String line = bufferedReader.readLine();
    while (line != null) {
      inputStringBuilder.append(line);
      inputStringBuilder.append('\n');
      line = bufferedReader.readLine();
    }
    new RestTemplateResponseLogger(
        response.getStatusCode().value(),
        response.getHeaders(),
        inputStringBuilder.toString(),
        tsRequest).logInfo();
  }
}
