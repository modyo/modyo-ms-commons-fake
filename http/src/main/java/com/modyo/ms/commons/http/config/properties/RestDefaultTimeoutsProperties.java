package com.modyo.ms.commons.http.config.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestDefaultTimeoutsProperties {

  private Integer connect = 200000;
  private Integer read = 200000;

}
