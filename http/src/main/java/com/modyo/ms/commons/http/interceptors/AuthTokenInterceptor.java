package com.modyo.ms.commons.http.interceptors;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class AuthTokenInterceptor implements ClientHttpRequestInterceptor {

  private String token;

  public AuthTokenInterceptor(String token) {
    this.token = token;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
      ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
    HttpHeaders headers = httpRequest.getHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, formatToken(token));
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
    return clientHttpRequestExecution.execute(httpRequest, bytes);
  }

  private static String formatToken(String token) {
    return ((token.split(" ")[0]).equals("Bearer") ? token : "Bearer " + token);
  }
}
