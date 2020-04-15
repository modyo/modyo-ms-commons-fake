package com.modyo.ms.commons.http.config.properties;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(value = "datasources.rest")
@Getter
@Setter
public class RestProperties {

  private RestDefaultTimeoutsProperties defaultTimeouts;
  private Map<String, RestWebServiceProperties> webServices;

}
