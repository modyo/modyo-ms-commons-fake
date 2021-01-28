package com.modyo.ms.commons.awsapigw.config;

import com.modyo.ms.commons.awsapigw.config.properties.ApiGwSwaggerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final ApiGwSwaggerProperties apiGwSwaggerProperties;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    String[] allowedOriginsArray = apiGwSwaggerProperties.getXAmazonApigatewayCors().getAllowOrigins()
        .toArray(new String[0]);
    registry.addMapping("/**")
        .allowedOrigins(allowedOriginsArray);
  }

  @Bean
  public RequestContextListener requestContextListener() {
    return new RequestContextListener();
  }
}
