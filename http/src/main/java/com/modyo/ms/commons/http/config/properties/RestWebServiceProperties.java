package com.modyo.ms.commons.http.config.properties;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.MultiValueMap;

@Getter
@Setter
public class RestWebServiceProperties {

  private String baseUrl;
  private String bearerAuth;
  private RestBasicAuthProperties basicAuth;
  private MultiValueMap<String, String> headers;
  private RestDefaultTimeoutsProperties timeouts;
  private Map<String, RestMethodProperties> methods;

}
