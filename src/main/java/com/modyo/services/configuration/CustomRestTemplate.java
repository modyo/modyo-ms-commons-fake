package com.modyo.services.configuration;

import com.modyo.services.interceptors.BasicAuthInterceptor;
import java.util.Collections;
import java.util.List;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Clase que construye la configuración de un Rest Template
 * <p>
 * Esta clase debe ser deprecada y posteriormente borrada !!!!!!!!!
 */
public class CustomRestTemplate extends RestTemplate {

  private CustomRestTemplate() {

  }

  private CustomRestTemplate(SimpleClientHttpRequestFactory clientHttpRequestFactory) {
    super(clientHttpRequestFactory);
  }

  private CustomRestTemplate(SimpleClientHttpRequestFactory clientHttpRequestFactory,
      String username,
      String password) {
    this(clientHttpRequestFactory);
    addAuthentication(username, password);
  }

  private void addAuthentication(String username, String password) {
    if (StringUtils.isEmpty(username)) {
      throw new RuntimeException("Username is mandatory for Basic Auth");
    }

    List<ClientHttpRequestInterceptor> interceptors =
        Collections.singletonList(new BasicAuthInterceptor(username, password));
    setRequestFactory(new InterceptingClientHttpRequestFactory(getRequestFactory(), interceptors));
  }

  public static class Builder {

    private int connectTimeout = 2000;
    private int readTimeout = 2000;
    private String username;
    private String password;

    public Builder connectTimeout(int connectTimeout) {
      this.connectTimeout = connectTimeout;
      return this;
    }

    public Builder readTimeout(int readTimeout) {
      this.readTimeout = readTimeout;
      return this;
    }

    public Builder username(String username) {
      this.username = username;
      return this;
    }

    public Builder password(String password) {
      this.password = password;
      return this;
    }

    public CustomRestTemplate build() {
      CustomRestTemplate result;
      SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
      clientHttpRequestFactory.setConnectTimeout(connectTimeout);
      clientHttpRequestFactory.setReadTimeout(readTimeout);
      if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
        result = new CustomRestTemplate(clientHttpRequestFactory, username, password);
      } else {
        result = new CustomRestTemplate(clientHttpRequestFactory);
      }
      return result;
    }
  }
}
