package com.modyo.ms.commons.awsapigw.config.properties;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
class JwtConfigurationProperties {

  @NotNull
  private String issuer;

  @NotNull
  @NotEmpty
  private List<String> audience;

}
