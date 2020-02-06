package com.modyo.services.configuration;

import com.modyo.services.interceptors.AuthTokenInterceptor;
import com.modyo.services.interceptors.BasicAuthInterceptor;
import com.modyo.services.interceptors.RestTemplateLoggerInterceptor;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Clase que engloba la configuración de un Rest Template.
 */
@Configuration
public class RestTemplates {

  @Value("${spring.logger.logRestTemplateRequestsEnabled}")
  private boolean logRestTemplateRequestsEnabled;

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  @Qualifier("restTemplate")
  public RestTemplate restTemplate() {
    return restTemplateTimeouts(2000, 5000);
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  @Qualifier("restTemplateBasicAuth")
  public RestTemplate restTemplateBasicAuth(String username, String password) {
    return restTemplateBasicAuthTimeouts(username, password, 2000, 5000);
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
    return restTemplateInterceptorTimeouts(
        null,
        connectTimeout,
        readTimeout
    );
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  @Qualifier("restTemplateBasicAuthTimeouts")
  public RestTemplate restTemplateBasicAuthTimeouts(String username, String password, int connectTimeout, int readTimeout) {
    return restTemplateInterceptorTimeouts(
        new BasicAuthInterceptor(username, password),
        connectTimeout,
        readTimeout
    );
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  @Qualifier("restTemplateAuthTokenTimeouts")
  public RestTemplate restTemplateAuthTokenTimeouts(String token, int connectTimeout, int readTimeout) {
    return restTemplateInterceptorTimeouts(
        new AuthTokenInterceptor(token),
        connectTimeout,
        readTimeout
    );
  }

  @Bean
  public RestTemplateLoggerInterceptor restTemplateLoggerInterceptor() {
    return new RestTemplateLoggerInterceptor();
  }

  private RestTemplate restTemplateInterceptorTimeouts(ClientHttpRequestInterceptor interceptor, int connectTimeout,
      int readTimeout) {
    SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
    simpleClientHttpRequestFactory.setConnectTimeout(connectTimeout);
    simpleClientHttpRequestFactory.setReadTimeout(readTimeout);
    RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(simpleClientHttpRequestFactory));
    List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
    if (interceptor != null) {
      interceptors.add(interceptor);
    }
    if (logRestTemplateRequestsEnabled) {
      interceptors.add(restTemplateLoggerInterceptor());
    }
    restTemplate.setRequestFactory(new InterceptingClientHttpRequestFactory(restTemplate.getRequestFactory(), interceptors));
    return restTemplate;
  }
}
