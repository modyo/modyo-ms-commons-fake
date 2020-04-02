package com.modyo.ms.commons.http.components;

import com.modyo.ms.commons.http.config.RestTemplateLoggerProperties;
import com.modyo.ms.commons.http.interceptors.AddRequestHeadersInterceptor;
import com.modyo.ms.commons.http.interceptors.RestTemplateLoggerInterceptor;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class CustomRestTemplateBuilder {

  private final RestTemplateLoggerProperties restTemplateLoggerProperties;
  private final RestTemplateLoggerInterceptor restTemplateLoggerInterceptor;

  private int connectTimeout = 150000;
  private int readTimeout = 150000;

  public CustomRestTemplateBuilder connectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
    return this;
  }

  public CustomRestTemplateBuilder readTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
    return this;
  }

  public RestTemplate build(HttpHeaders headers) {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setRequestFactory(
        new InterceptingClientHttpRequestFactory(
            buildRequestFactory(),
            buildInterceptors(headers))
    );
    return restTemplate;
  }

  public RestTemplate build() {
    return build(null);
  }

  private BufferingClientHttpRequestFactory buildRequestFactory() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(connectTimeout);
    factory.setReadTimeout(readTimeout);
    return new BufferingClientHttpRequestFactory(factory);
  }

  private List<ClientHttpRequestInterceptor> buildInterceptors(HttpHeaders headers) {
    List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
    if (headers != null && !headers.isEmpty()) {
      interceptors.add(new AddRequestHeadersInterceptor(headers));
    }
    if (restTemplateLoggerProperties.isEnabled()) {
      interceptors.add(restTemplateLoggerInterceptor);
    }
    return interceptors;
  }

}
