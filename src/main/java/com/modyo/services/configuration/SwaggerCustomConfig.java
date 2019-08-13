package com.modyo.services.configuration;

import static springfox.documentation.builders.PathSelectors.regex;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Header;
import springfox.documentation.service.ListVendorExtension;
import springfox.documentation.service.ObjectVendorExtension;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configuracion de Spring boot que permite generar documentacion Swagger 2
 */

@Configuration
@Import({HandledHttpStatusListConfiguration.class})
@EnableSwagger2
public class SwaggerCustomConfig {

  // Auth Type
  private static final String AWS_X_AMAZON_APIGATEWAY_AUTHTYPE = "x-amazon-apigateway-authtype";

  // Authorizer
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String DEFAULT_INCLUDE_PATTERN = "/.*";
  private static final String AWS_X_AMAZON_APIGATEWAY_AUTHORIZER = "x-amazon-apigateway-authorizer";
  private static final String AWS_X_AMAZON_APIGATEWAY_AUTHORIZER_TYPE = "type";
  private static final String AWS_X_AMAZON_APIGATEWAY_AUTHORIZER_AUTHORIZER_URI = "authorizerUri";
  private static final String AWS_X_AMAZON_APIGATEWAY_AUTHORIZER_AUTHORIZERCREDENTIALS = "authorizerCredentials";
  private static final String AWS_X_AMAZON_APIGATEWAY_AUTHORIZER_IDENTITYVALIDATIONEXPRESSION = "identityValidationExpression";
  private static final String AWS_X_AMAZON_APIGATEWAY_AUTHORIZER_AUTHORIZERRESULTTTLINSECONDS = "authorizerResultTtlInSeconds";

  private static final String CUSTOM = "CUSTOM";
  private static final String TOKEN = "token";
  // TODO: Refactorizar para usar las mismas constantes, ojala en la clase Constantes
  private static final String HEADER_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
  private static final String HEADER_ALLOW_METHODS = "Access-Control-Allow-Methods";
  private static final String HEADER_ALLOW_HEADERS = "Access-Control-Allow-Headers";
  private static final String HEADER_VARY = "Vary";
  
  @Autowired
  private List<HttpStatus> handledHttpStatusList;
  @Value("${aws.apigateway.uriAuthorizer}")
  private String uriLambdaAuthorizer;
  @Value("${aws.apigateway.authorizerCredentials}")
  private String authorizerCredentials;
  @Value("${aws.apigateway.identityValidationExpression}")
  private String identityValidationExpression;
  @Value("${aws.apigateway.authorizerResultTtlInSeconds}")
  private String authorizerResultTtlInSeconds;
  @Value("${aws.apigateway.apiName}")
  private String apiName;
  @Value("${aws.apigateway.authorizerName}")
  private String authorizerName;
  @Value("${modo.privado}")
  private boolean modoPrivado;
  @Value("${swagger.contact.name}")
  private String swaggerContactName;
  @Value("${swagger.contact.url}")
  private String swaggerContactUrl;
  @Value("${swagger.contact.email}")
  private String swaggerContactEmail;
  @Value("${swagger.contact.version}")
  private String swaggerContactVersion;

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
        .paths(PathSelectors.any()).build();
    // TODO: Mejorar esto para permitir que sea a nivel de endpoint
    if (modoPrivado) {
      docket.securityContexts(Lists.newArrayList(securityContext()));
      docket.securitySchemes(Lists.newArrayList(apiKey()));
    }
    return docket;
  }

  private ApiKey apiKey() {
    List<VendorExtension> listadoVendorExtension = new ArrayList<>();
    listadoVendorExtension.add(new StringVendorExtension(AWS_X_AMAZON_APIGATEWAY_AUTHTYPE, CUSTOM));

    ObjectVendorExtension objVendorExtension = new ObjectVendorExtension(AWS_X_AMAZON_APIGATEWAY_AUTHORIZER);
    objVendorExtension.addProperty(new StringVendorExtension(AWS_X_AMAZON_APIGATEWAY_AUTHORIZER_TYPE, TOKEN));
    objVendorExtension.addProperty(new StringVendorExtension(AWS_X_AMAZON_APIGATEWAY_AUTHORIZER_AUTHORIZER_URI, uriLambdaAuthorizer));
    objVendorExtension.addProperty(new StringVendorExtension(AWS_X_AMAZON_APIGATEWAY_AUTHORIZER_AUTHORIZERCREDENTIALS, authorizerCredentials));
    objVendorExtension.addProperty(new StringVendorExtension(AWS_X_AMAZON_APIGATEWAY_AUTHORIZER_IDENTITYVALIDATIONEXPRESSION, identityValidationExpression));
    objVendorExtension.addProperty(new StringVendorExtension(AWS_X_AMAZON_APIGATEWAY_AUTHORIZER_AUTHORIZERRESULTTTLINSECONDS, authorizerResultTtlInSeconds));

    listadoVendorExtension.add(objVendorExtension);

    return new ApiKey(authorizerName, AUTHORIZATION_HEADER, "header",
        listadoVendorExtension);
  }

  /**
   * Aqui esta la lógica de evitar agregar el lambda authorizer al OPTIONS
   */
  private SecurityContext securityContext() {
    Predicate<HttpMethod> predicate = input -> !input.name().equalsIgnoreCase(HttpMethod.OPTIONS.name());
    return SecurityContext.builder()
        .securityReferences(defaultAuth())
        .forPaths(regex(DEFAULT_INCLUDE_PATTERN))
        .forHttpMethods(predicate::test)
        .build();
  }

  private List<SecurityReference> defaultAuth() {
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[0];
    return Lists.newArrayList(
        new SecurityReference(authorizerName, authorizationScopes));
  }

  private ApiInfo getApiInfo() {
    List<String> listadoExtensiones = new ArrayList<>();
// TODO: Manejar este listado igual en el plugin
    listadoExtensiones.add(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    listadoExtensiones.add(MediaType.APPLICATION_PDF_VALUE);
    listadoExtensiones.add(MediaType.IMAGE_PNG_VALUE);
    listadoExtensiones.add(MediaType.IMAGE_GIF_VALUE);
    listadoExtensiones.add(MediaType.IMAGE_JPEG_VALUE);
    listadoExtensiones.add(MediaType.MULTIPART_FORM_DATA_VALUE);

    ListVendorExtension<String> listadoVendorExtension = new
        ListVendorExtension("x-amazon-apigateway-binary-media-types", listadoExtensiones);

    return new ApiInfo(
        apiName,
        "",
        swaggerContactVersion,
        "",
        new Contact(swaggerContactName, swaggerContactUrl, swaggerContactEmail),
        "",
        "",
        Collections.singletonList(listadoVendorExtension)
    );
  }

  private List<ResponseMessage> getCustomizedResponseMessages() {
    Map<String, Header> mapaHeaders = new HashMap<>();

    ModelRef mr = new ModelRef("string");
    mapaHeaders.put(HEADER_ALLOW_ORIGIN, new Header(HEADER_ALLOW_ORIGIN, "", mr));
    mapaHeaders.put(HEADER_VARY, new Header(HEADER_VARY, "", mr));
    mapaHeaders.put("Content-Disposition", new Header("Content-Disposition", "", mr));
    mapaHeaders.put("Content-Length", new Header("Content-Length", "", mr));
    mapaHeaders.put("Content-Type", new Header("Content-Type", "", mr));
    mapaHeaders.put("X-Application-Name", new Header("X-Application-Name", "", mr));
    mapaHeaders.put("X-Parents-Correlation-Ids", new Header("X-Parents-Correlation-Ids", "", mr));
    mapaHeaders.put("X-Correlation-ID", new Header("X-Correlation-ID", "", mr));

    return handledHttpStatusList.stream().map(httpStatus -> new ResponseMessageBuilder()
        .code(httpStatus.value())
        .message(httpStatus.getReasonPhrase()).headersWithDescription(mapaHeaders)
        .build()).collect(Collectors.toList());
  }

  private List<ResponseMessage> getCustomizedResponseMessagesOptions() {
    Map<String, Header> mapaHeaders = new HashMap<>();

    ModelRef mr = new ModelRef("string");
    Header h = new Header(HEADER_ALLOW_ORIGIN, "", mr);
    mapaHeaders.put(HEADER_ALLOW_ORIGIN, h);
    Header h1 = new Header(HEADER_ALLOW_METHODS, "", mr);
    mapaHeaders.put(HEADER_ALLOW_METHODS, h1);
    Header h2 = new Header(HEADER_ALLOW_HEADERS, "", mr);
    mapaHeaders.put(HEADER_ALLOW_HEADERS, h2);
    Header h3 = new Header(HEADER_VARY, "", mr);
    mapaHeaders.put(HEADER_VARY, h3);

    ResponseMessage rm = new ResponseMessageBuilder().code(HttpStatus.OK.value())
        .headersWithDescription(mapaHeaders).build();

    return Lists.newArrayList(rm);
  }


}
