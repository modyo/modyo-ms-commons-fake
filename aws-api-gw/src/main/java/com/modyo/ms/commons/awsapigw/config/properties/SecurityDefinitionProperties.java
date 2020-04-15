package com.modyo.ms.commons.awsapigw.config.properties;

import io.swagger.models.auth.ApiKeyAuthDefinition;
import io.swagger.models.auth.In;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Setter
@Getter
@Validated
public class SecurityDefinitionProperties {

  @NotNull private String definitionName;

  @Pattern(
      regexp="^(apiKey)$", // TODO support for "basic" and "oauth2" types
      message = "invalid or unsupported security definition type"
  )
  private String type;

  @NotNull private String name;
  @NotNull private String in;
  @NotNull private String xAmazonApigatewayAuthtype;
  @NotNull private ApiGatewayAuthorizerProperties xAmazonApigatewayAuthorizer;

  @NotNull
  @NotEmpty
  private List<String> operations;

  public ApiKeyAuthDefinition getApiKeyAuthDefinition() {
    ApiKeyAuthDefinition authDefinition = new ApiKeyAuthDefinition();
    authDefinition.setName(name);
    authDefinition.setIn(In.forValue(in));
    authDefinition.setVendorExtension(
        "x-amazon-apigateway-authtype",
        xAmazonApigatewayAuthtype);
    authDefinition.setVendorExtension(
        "x-amazon-apigateway-authorizer",
        xAmazonApigatewayAuthorizer);
    return authDefinition;
  }

}
