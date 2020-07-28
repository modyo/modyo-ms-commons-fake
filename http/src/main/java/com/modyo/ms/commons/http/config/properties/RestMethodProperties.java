package com.modyo.ms.commons.http.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

@Getter
@Setter
public class RestMethodProperties {

  private String path;
  private HttpMethod httpMethod;

}
