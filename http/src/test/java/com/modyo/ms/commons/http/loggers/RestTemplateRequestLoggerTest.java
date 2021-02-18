package com.modyo.ms.commons.http.loggers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.List;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public class RestTemplateRequestLoggerTest {

  @Test
  public void constructor_obfuscateHeader() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Public", "show");
    httpHeaders.add("Private", "hide");
    RestTemplateRequestLogger restTemplateRequestLogger = new RestTemplateRequestLogger(
        HttpMethod.GET.toString(),
        "my/uri",
        httpHeaders,
        null,
        List.of("Private")
    );

    assertThat(restTemplateRequestLogger.getHeaders().get("Public"), is(List.of("show")));
    assertThat(restTemplateRequestLogger.getHeaders().get("Private"), is(List.of("*********")));
  }

  @Test
  public void constructor_obfuscateBody() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("ObfuscateBodyParams", "private");
    RestTemplateRequestLogger restTemplateRequestLogger = new RestTemplateRequestLogger(
        HttpMethod.GET.toString(),
        "my/uri",
        httpHeaders,
        "{\"private\":\"show\"}",
        null
    );

    assertThat(restTemplateRequestLogger.getBody(),
        is("{\"private\":\"*********\"}"));
  }

}
