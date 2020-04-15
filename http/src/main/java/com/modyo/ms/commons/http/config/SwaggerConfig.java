package com.modyo.ms.commons.http.config;

import static com.google.common.base.Predicates.not;

import com.modyo.ms.commons.core.constants.HandledHttpStatus;
import com.modyo.ms.commons.http.config.properties.SwaggerApiInfoProperties;
import com.modyo.ms.commons.http.constants.CustomHttpHeaders;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Header;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configuracion de Spring boot que permite generar documentacion Swagger 2
 */

@Configuration
@EnableSwagger2
@RequiredArgsConstructor
public class SwaggerConfig {

  private final SwaggerApiInfoProperties swaggerApiInfoProperties;

  private static final List<String> HEADERS = List.of(
      HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS,
      HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
      HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
      HttpHeaders.CONTENT_DISPOSITION,
      HttpHeaders.CONTENT_LENGTH,
      HttpHeaders.CONTENT_TYPE,
      CustomHttpHeaders.APPLICATION_NAME,
      CustomHttpHeaders.CORRELATION_ID);

  private static final List<RequestMethod> REQUEST_METHODS = List.of(
      RequestMethod.GET,
      RequestMethod.POST,
      RequestMethod.PUT,
      RequestMethod.PATCH,
      RequestMethod.DELETE);

  @Bean
  public Docket api() {
    Docket docket = new Docket(DocumentationType.SWAGGER_2)
        .pathMapping("/")
        .apiInfo(swaggerApiInfoProperties.getApiInfo());
    setGlobalResponseMessages(docket);
    docket = docket.select()
        .apis(not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
        .paths(PathSelectors.any())
        .build();
    return docket;
  }

  private void setGlobalResponseMessages(Docket docket) {
    REQUEST_METHODS.forEach(method -> docket.globalResponseMessage(method, getResponseMessages()));
  }

  private List<ResponseMessage> getResponseMessages() {
    return HandledHttpStatus.getList().stream()
        .map(httpStatus -> new ResponseMessageBuilder()
            .code(httpStatus.value())
            .message(httpStatus.getReasonPhrase())
            .headersWithDescription(buildHeadersMap())
            .build())
        .collect(Collectors.toList());
  }

  private Map<String, Header> buildHeadersMap() {
    return HEADERS.stream()
        .collect(Collectors.toMap(
            header -> header,
            header -> new Header(header, "", new ModelRef("string"))
        ));
  }

}
