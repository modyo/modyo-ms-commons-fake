package com.modyo.services.configuration;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Header;
import springfox.documentation.service.ListVendorExtension;
import springfox.documentation.service.ObjectVendorExtension;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configuracion de Spring boot que permite generar documentacion Swagger 2
 */

@Configuration
@EnableSwagger2
public class SwaggerCustomConfig {

  private static final String[] MEDIA_TYPES = {
    MediaType.APPLICATION_OCTET_STREAM_VALUE,
    MediaType.APPLICATION_PDF_VALUE,
    MediaType.IMAGE_PNG_VALUE,
    MediaType.IMAGE_GIF_VALUE,
    MediaType.IMAGE_JPEG_VALUE,
    MediaType.MULTIPART_FORM_DATA_VALUE
  };
  private static final String[] HEADERS = {
    "Access-Control-Allow-Origin",
    "Content-Disposition",
    "Content-Length",
    "Content-Type",
    "X-Application-Name",
    "X-Parents-Correlation-Ids",
    "X-Correlation-ID"
  };
  private static final String[] OPTIONS_HEADERS = {
    "Access-Control-Allow-Origin",
    "Access-Control-Allow-Methods",
    "Access-Control-Allow-Headers",
    "Vary"
  };

  @Value("${aws.apigateway.uriAuthorizer}")
  private String uriLambdaAuthorizer;
  @Value("${aws.apigateway.authorizerCredentials}")
  private String authorizerCredentials;
  @Value("${aws.apigateway.identityValidationExpression}")
  private String identityValidationExpression;
  @Value("${aws.apigateway.authorizerResultTtlInSeconds}")
  private String authorizerResultTtlInSeconds;
  @Value("${aws.apigateway.name}")
  private String apigatewayName;
  @Value("${aws.apigateway.authorizerName}")
  private String authorizerName;
  @Value("${swagger.contact.name}")
  private String swaggerContactName;
  @Value("${swagger.contact.url}")
  private String swaggerContactUrl;
  @Value("${swagger.contact.email}")
  private String swaggerContactEmail;
  @Value("${spring.application.version}")
  private String applicationVersion;
  @Value("${spring.main.requiresLambdaAuthorization}")
  private boolean requiresLambdaAuthorization;

  @Bean
  public Docket api() {
    Docket docket = new Docket(DocumentationType.SWAGGER_2)
      .pathMapping("/")
      .apiInfo(getApiInfo())
      .globalResponseMessage(RequestMethod.GET, getCustomizedResponseMessages())
      .globalResponseMessage(RequestMethod.POST, getCustomizedResponseMessages())
      .globalResponseMessage(RequestMethod.PUT, getCustomizedResponseMessages())
      .globalResponseMessage(RequestMethod.PATCH, getCustomizedResponseMessages())
      .globalResponseMessage(RequestMethod.DELETE, getCustomizedResponseMessages())
      .globalResponseMessage(RequestMethod.OPTIONS, getCustomizedResponseMessagesOptions());
    docket = docket.select()
      .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
      .paths(PathSelectors.any())
      .build();
    if (requiresLambdaAuthorization) {
      docket.securitySchemes(Lists.newArrayList(apiKey()));
    }
    return docket;
  }

  private ApiKey apiKey() {
    List<VendorExtension> vendorExtensionList = new ArrayList<>();
    vendorExtensionList.add(new StringVendorExtension("x-amazon-apigateway-authtype", "CUSTOM"));
    ObjectVendorExtension objVendorExtension = new ObjectVendorExtension("x-amazon-apigateway-authorizer");
    objVendorExtension.addProperty(new StringVendorExtension("type", "token"));
    objVendorExtension.addProperty(new StringVendorExtension("authorizerUri", uriLambdaAuthorizer));
    objVendorExtension.addProperty(new StringVendorExtension("authorizerCredentials", authorizerCredentials));
    objVendorExtension.addProperty(new StringVendorExtension("identityValidationExpression", identityValidationExpression));
    objVendorExtension.addProperty(new StringVendorExtension("authorizerResultTtlInSeconds", authorizerResultTtlInSeconds));
    vendorExtensionList.add(objVendorExtension);
    return new ApiKey(authorizerName, "Authorization", "header", vendorExtensionList);
  }

  private ApiInfo getApiInfo() {
    ListVendorExtension<String> listVendorExtension = new ListVendorExtension<>(
      "x-amazon-apigateway-binary-media-types",
      Arrays.asList(MEDIA_TYPES)
    );
    Contact contact = new Contact(swaggerContactName, swaggerContactUrl, swaggerContactEmail);
    return new ApiInfo(
      apigatewayName,
      "",
      applicationVersion,
      "",
      contact,
      "",
      "",
      Collections.singletonList(listVendorExtension)
    );
  }

  private List<ResponseMessage> getCustomizedResponseMessages() {
    return HandledHttpStatus.getList().stream().map(httpStatus -> new ResponseMessageBuilder()
      .code(httpStatus.value())
      .message(httpStatus.getReasonPhrase()).headersWithDescription(buildHeadersMap(HEADERS))
      .build()).collect(Collectors.toList());
  }

  private List<ResponseMessage> getCustomizedResponseMessagesOptions() {
    return Lists.newArrayList(new ResponseMessageBuilder()
      .code(HttpStatus.OK.value())
      .headersWithDescription(buildHeadersMap(OPTIONS_HEADERS))
      .build());
  }

  private Map<String, Header> buildHeadersMap(String[] headers) {
    return Arrays.stream(headers)
      .collect(Collectors.toMap(
        header -> header,
        header -> new Header(header, "", new ModelRef("string"))
      ));
  }

}
