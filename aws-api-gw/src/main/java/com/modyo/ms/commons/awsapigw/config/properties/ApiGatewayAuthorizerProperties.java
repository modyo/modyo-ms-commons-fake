package com.modyo.ms.commons.awsapigw.config.properties;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
public class ApiGatewayAuthorizerProperties {

  @JsonInclude(JsonInclude.Include.NON_NULL)

  @NotNull private String type;
  @NotNull private String authorizerUri;
  @NotNull private String authorizerCredentials;
  @NotNull private String identityValidationExpression;
  @NotNull private String authorizerResultTtlInSeconds;
  private String identitySource;
  @Valid private JwtConfigurationProperties jwtConfiguration;

}
