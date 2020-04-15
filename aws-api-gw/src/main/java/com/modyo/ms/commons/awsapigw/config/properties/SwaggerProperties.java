package com.modyo.ms.commons.awsapigw.config.properties;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "commons.aws-api-gw.swagger")
@Getter
@Setter
@Validated
public class SwaggerProperties {

  private List<String> xAmazonApigatewayBinaryMediaTypes = new ArrayList<>();
  @NotNull private IntegrationProperties xAmazonApigatewayIntegration;
  private List<SecurityDefinitionProperties> securityDefinitions = new ArrayList<>();
  private CorsProperties xAmazonApigatewayCors = new CorsProperties();

}
