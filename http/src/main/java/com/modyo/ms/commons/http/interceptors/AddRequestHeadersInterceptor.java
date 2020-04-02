package com.modyo.ms.commons.http.interceptors;

import java.io.IOException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@RequiredArgsConstructor
public class AddRequestHeadersInterceptor implements ClientHttpRequestInterceptor {

  private final HttpHeaders headers;

  @Override
  public ClientHttpResponse intercept(
      HttpRequest httpRequest,
      byte[] bytes,
      ClientHttpRequestExecution clientHttpRequestExecution
  )throws IOException {
    HttpHeaders requestHeaders = httpRequest.getHeaders();
    requestHeaders.addAll(headers);
    return clientHttpRequestExecution.execute(httpRequest, bytes);
  }
}
