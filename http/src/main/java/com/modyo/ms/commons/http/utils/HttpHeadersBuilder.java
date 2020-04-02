package com.modyo.ms.commons.http.utils;

import org.springframework.http.HttpHeaders;
import org.apache.tomcat.util.codec.binary.Base64;

public class HttpHeadersBuilder {

  private HttpHeaders headers = new HttpHeaders();

  public HttpHeadersBuilder basicAuthCredentials(String username, String password) {
    headers.add(
        HttpHeaders.AUTHORIZATION,
        formatBasicAuthHeaderValue(encodedCredentials(username, password)));
    return this;
  }

  public HttpHeadersBuilder accessToken(String accessToken) {
    headers.add(
        HttpHeaders.AUTHORIZATION,
        formatBearerTokenAuthHeaderValue(accessToken));
    return this;
  }

  public HttpHeadersBuilder header(String name, String value) {
    headers.add(name, value);
    return this;
  }

  public HttpHeaders build() {
    return headers;
  }

  private String formatBasicAuthHeaderValue(String encodedCredentials) {
    return "Basic " + encodedCredentials;
  }

  private String encodedCredentials(String username, String password) {
    return new Base64().encodeToString((username + ":" + password).getBytes());
  }

  private String formatBearerTokenAuthHeaderValue(String accessToken) {
    return "Bearer " + accessToken;
  }

}
