package com.modyo.ms.commons.awsapigw.config;

import com.modyo.ms.commons.awsapigw.config.properties.ApiGwSwaggerProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final ApiGwSwaggerProperties apiGwSwaggerProperties;

  @Value("${commons.aws-api-gw.swagger.x-amazon-apigateway-cors.allow-origins}")
  private String[] allowOrigins;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins(allowOrigins)
        .allowedMethods(listToArray(apiGwSwaggerProperties.getXAmazonApigatewayCors().getAllowMethods()))
        .allowedHeaders(listToArray(apiGwSwaggerProperties.getXAmazonApigatewayCors().getAllowHeaders()))
        ;
  }



  @Bean
  public RequestContextListener requestContextListener() {
    return new RequestContextListener();
  }

  private String[] listToArray(List<String> list) {
    return list.toArray(new String[0]);
  }
}
