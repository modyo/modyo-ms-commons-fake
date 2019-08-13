package com.modyo.services.configuration;

import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class BasicAuthTokenInterceptor implements ClientHttpRequestInterceptor {

  private String token;

  public BasicAuthTokenInterceptor(String token) {
    this.token = token;
  }

  @Override
  public ClientHttpResponse intercept(
      HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution
  ) throws IOException {
    HttpHeaders headers = httpRequest.getHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, encodeCredentialsForBasicAuth(token));
    return clientHttpRequestExecution.execute(httpRequest, bytes);
  }

  private static String encodeCredentialsForBasicAuth(String token) {
    return "Bearer " + token;
  }

}
