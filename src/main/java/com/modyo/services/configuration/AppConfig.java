package com.modyo.services.configuration;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Clase que engloba la configuración de un Rest Template.
 */
@Configuration
public class AppConfig {

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  @Qualifier("restTemplate")
  public RestTemplate restTemplate() {
    return restTemplateTimeouts(2000, 2000);
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  @Qualifier("restTemplateBasicAuth")
  public RestTemplate restTemplateBasicAuth(String username, String password) {
    return restTemplateBasicAuthTimeouts(username, password, 2000, 2000);
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  @Qualifier("restTemplateAuthToken")
  public RestTemplate restTemplateAuthToken(String token) {
    return restTemplateAuthTokenTimeouts(token, 2000, 2000);
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  @Qualifier("restTemplateTimeouts")
  public RestTemplate restTemplateTimeouts(int connectTimeout, int readTimeout) {
    SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
    clientHttpRequestFactory.setConnectTimeout(connectTimeout);
    clientHttpRequestFactory.setReadTimeout(readTimeout);
    RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
    return restTemplate;
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  @Qualifier("restTemplateBasicAuthTimeouts")
  public RestTemplate restTemplateBasicAuthTimeouts(String username, String password,
      int connectTimeout, int readTimeout) {
    SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
    clientHttpRequestFactory.setConnectTimeout(connectTimeout);
    clientHttpRequestFactory.setReadTimeout(readTimeout);
    RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
    List<ClientHttpRequestInterceptor> interceptors =
        Collections.singletonList(new BasicAuthInterceptor(username, password));
    restTemplate.setRequestFactory(
        new InterceptingClientHttpRequestFactory(restTemplate.getRequestFactory(), interceptors));
    return restTemplate;
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  @Qualifier("restTemplateAuthTokenTimeouts")
  public RestTemplate restTemplateAuthTokenTimeouts(String token, int connectTimeout,
      int readTimeout) {
    SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
    clientHttpRequestFactory.setConnectTimeout(connectTimeout);
    clientHttpRequestFactory.setReadTimeout(readTimeout);
    RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
    List<ClientHttpRequestInterceptor> interceptors =
        Collections.singletonList(new BasicAuthTokenInterceptor(token));
    restTemplate.setRequestFactory(
        new InterceptingClientHttpRequestFactory(restTemplate.getRequestFactory(), interceptors));
    return restTemplate;
  }
}
