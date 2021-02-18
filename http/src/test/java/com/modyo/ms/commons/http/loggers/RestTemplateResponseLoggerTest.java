package com.modyo.ms.commons.http.loggers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

public class RestTemplateResponseLoggerTest {

  @Test
  public void constructor_obfuscateBody() {
    HttpHeaders httpHeaders = new HttpHeaders();
    HttpHeaders requestHeaders = new HttpHeaders();
    Date now = new Date();
    requestHeaders.add("ObfuscateResponseBodyParams", "private");
    RestTemplateResponseLogger restTemplateResponseLogger = new RestTemplateResponseLogger(
        200,
        httpHeaders,
        requestHeaders,
        "{\"private\":\"show\"}",
        now
    );

    assertThat(restTemplateResponseLogger.getStatus(), is(200));
    assertThat(restTemplateResponseLogger.getHeaders(), is(httpHeaders));
    assertThat(restTemplateResponseLogger.getTimeStampRequest(), is(now));
    assertThat(restTemplateResponseLogger.getBody(),
        is("{\"private\":\"*********\"}"));
  }

}
