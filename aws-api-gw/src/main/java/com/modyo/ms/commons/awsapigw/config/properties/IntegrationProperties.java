package com.modyo.ms.commons.awsapigw.config.properties;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
public class IntegrationProperties {

  @NotNull private String connectionId;
  @NotNull private String type;
  @NotNull private String connectionType;
  @NotNull private String uri;

}
