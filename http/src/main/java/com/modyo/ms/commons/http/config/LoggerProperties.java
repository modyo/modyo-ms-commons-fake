package com.modyo.ms.commons.http.config;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
abstract class LoggerProperties {

  private boolean enabled = false;
  private ObfuscateProperties obfuscate = new ObfuscateProperties();

  @Setter
  @Getter
  public class ObfuscateProperties {

    RequestProperties request = new RequestProperties();

  }

  @Setter
  @Getter
  public class RequestProperties {

    private List<String> headers = Collections.emptyList();

  }

}
