package com.modyo.ms.commons.http.config;

import com.modyo.ms.commons.http.config.properties.RestProperties;
import com.modyo.ms.commons.http.config.properties.RestTemplateLoggerProperties;
import com.modyo.ms.commons.http.interceptors.RestTemplateLoggerInterceptor;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RestTemplateBuilderConfig {

  private final RestProperties restProperties;
  private final RestTemplateLoggerProperties loggerProperties;
  private final RestTemplateLoggerInterceptor loggerInterceptor;

  @Bean
  @Primary
  @Scope("prototype")
  RestTemplateBuilder restTemplateBuilder() {
    return loggerProperties.isEnabled()
    ? new RestTemplateBuilder()
        .requestFactory(this::buildRequestFactory)
        .interceptors(List.of(loggerInterceptor))
    : new RestTemplateBuilder()
        .setConnectTimeout(Duration.ofMillis(restProperties.getDefaultTimeouts().getConnect()))
        .setReadTimeout(Duration.ofMillis(restProperties.getDefaultTimeouts().getRead()));
  }

  @Bean
  RestTemplate restTemplate() {
    return restTemplateBuilder().build();
  }

  private BufferingClientHttpRequestFactory buildRequestFactory() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(restProperties.getDefaultTimeouts().getConnect());
    factory.setReadTimeout(restProperties.getDefaultTimeouts().getRead());
    return new BufferingClientHttpRequestFactory(factory);
  }

}
