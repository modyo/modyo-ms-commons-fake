package com.modyo.ms.commons.http.config.properties;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

@Getter
@Setter
public class RestMethodProperties {

  private String path;
  private HttpMethod httpMethod;
  private MultiValueMap<String, String> headers;
  private Map<String, String> body;

}
