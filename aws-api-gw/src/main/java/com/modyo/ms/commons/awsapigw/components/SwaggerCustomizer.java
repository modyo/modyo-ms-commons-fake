package com.modyo.ms.commons.awsapigw.components;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.modyo.ms.commons.awsapigw.constants.AwsExtensionsPrefixes.I_REQ_PREFIX;
import static com.modyo.ms.commons.awsapigw.constants.AwsExtensionsPrefixes.I_RESP_H_PREFIX;
import static com.modyo.ms.commons.awsapigw.constants.AwsExtensionsPrefixes.M_REQ_PREFIX;
import static com.modyo.ms.commons.awsapigw.constants.AwsExtensionsPrefixes.M_RESP_H_PREFIX;
import static springfox.documentation.swagger.common.HostNameProvider.componentsFrom;

import com.google.common.base.Strings;
import com.modyo.ms.commons.awsapigw.config.properties.ApiGwSwaggerProperties;
import com.modyo.ms.commons.awsapigw.config.properties.SecurityDefinitionProperties;
import com.modyo.ms.commons.core.constants.HandledHttpStatus;
import com.modyo.ms.commons.http.constants.CustomHttpHeaders;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.auth.SecuritySchemeDefinition;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

@RequiredArgsConstructor
@Component
public class SwaggerCustomizer {

  @Value("${server.port}")
  private String serverPort;

  private final String hostNameOverride;
  private final ApiGwSwaggerProperties swaggerProperties;
  private final OptionsMockOperationBuilder optionsMockOperationBuilder;
  private final ServiceModelToSwagger2Mapper mapper;
  private final DocumentationCache documentationCache;

  private Swagger swagger;

  private static final List<String> RESPONSE_PARAMETERS = List.of(
      HttpHeaders.CONTENT_DISPOSITION,
      HttpHeaders.CONTENT_LENGTH,
      HttpHeaders.CONTENT_TYPE,
      CustomHttpHeaders.CORRELATION_ID,
      CustomHttpHeaders.APPLICATION_NAME
  );

  @Autowired
  public SwaggerCustomizer(
      Environment environment,
      ApiGwSwaggerProperties swaggerProperties,
      OptionsMockOperationBuilder optionsMockOperationBuilder,
      ServiceModelToSwagger2Mapper mapper,
      DocumentationCache documentationCache) {
    this.hostNameOverride =
        environment.getProperty(
            "springfox.documentation.swagger.v2.host",
            "DEFAULT");
    this.swaggerProperties = swaggerProperties;
    this.optionsMockOperationBuilder = optionsMockOperationBuilder;
    this.mapper = mapper;
    this.documentationCache = documentationCache;
  }

  public Swagger getCustomSwagger(HttpServletRequest request) {
    Documentation documentation = documentationCache.documentationByGroup("default");
    swagger = mapper.mapDocumentation(documentation);
    setSwaggerHostAndBasePath(request);
    swagger.getInfo().setTitle(swaggerProperties.getApigatewayName());
    setSwaggerBinaryMediaTypes();
    swagger.getPaths().forEach(this::addVendorExtensions);
    if (swaggerProperties.getXAmazonApigatewayCors().getEnableMockOptionMethods()) {
      swagger.getPaths().forEach((pathString, pathObject) -> pathObject
          .setOptions(optionsMockOperationBuilder.buildOptionsOperation(pathObject)));
    }
    setSwaggerApiGwCors();
    swagger.setSecurityDefinitions(getSecurityDefinitions());
    swagger.getDefinitions().forEach((modelName, model) -> {
      model.getProperties().forEach((propertyName, property) -> {
        property.setExample((Object)null);
      });
    });
    return swagger;
  }

  private void setSwaggerHostAndBasePath(HttpServletRequest request) {
    UriComponents uriComponents = componentsFrom(request, swagger.getBasePath());
    swagger.basePath(Strings.isNullOrEmpty(uriComponents.getPath())
        ? "/"
        : uriComponents.getPath());

    if (isNullOrEmpty(swagger.getHost())) {
      swagger.host(hostName(uriComponents));
    }
  }

  private void setSwaggerBinaryMediaTypes() {
    if(!swaggerProperties.getXAmazonApigatewayBinaryMediaTypes().isEmpty()) {
      swagger.getInfo().setVendorExtension(
          "x-amazon-apigateway-binary-media-types",
          swaggerProperties.getXAmazonApigatewayBinaryMediaTypes());
    }
  }

  private void setSwaggerApiGwCors() {
    if (swaggerProperties.getXAmazonApigatewayCors().getEnableGeneralConfiguration()) {
      swagger.setVendorExtension(
          "x-amazon-apigateway-cors",
          swaggerProperties.getXAmazonApigatewayCors());
    }
  }

  private String hostName(UriComponents uriComponents) {
    if ("DEFAULT".equals(hostNameOverride)) {
      String host = uriComponents.getHost();
      int port = uriComponents.getPort();
      if (port > -1) {
        return String.format("%s:%d", host, port);
      }
      return host;
    }
    return hostNameOverride;
  }

  private void addVendorExtensions(String pathString, Path pathObject) {
    pathObject.getOperationMap().forEach((method, operation) -> {
      Map<String, Object> vendorExtensions = new HashMap<>();
      vendorExtensions.put(
          "x-amazon-apigateway-integration",
          buildAwsIntegrationExtension(method.name(), operation, pathString));
      operation.setVendorExtensions(vendorExtensions);
    });
  }

  private Map<String, Object> buildAwsIntegrationExtension(
      String method,
      Operation operation,
      String pathString
  ) {
    return Map.of(
        "httpMethod",
        method,
        "connectionId",
        swaggerProperties.getXAmazonApigatewayIntegration().getConnectionId(),
        "connectionType",
        swaggerProperties.getXAmazonApigatewayIntegration().getConnectionType(),
        "type",
        swaggerProperties.getXAmazonApigatewayIntegration().getType(),
        "uri",
        buildApiGatewayIntegrarionUri(pathString),
        "requestParameters",
        buildRequestParameters(operation),
        "responses",
        getResponseParameters());
  }

  private String buildApiGatewayIntegrarionUri(String pathString) {
    return swaggerProperties.getXAmazonApigatewayIntegration().getUri() + ":" +
        serverPort +
        (swagger.getBasePath().equals("/") ? "" : swagger.getBasePath()) +
        pathString;
  }

  private Map<String, Object> buildRequestParameters(Operation operation) {
    return operation.getParameters().stream()
        .collect(Collectors.toMap(
            parameter -> I_REQ_PREFIX + getParamType(parameter.getIn()) + "." + parameter.getName(),
            parameter -> M_REQ_PREFIX + getParamType(parameter.getIn()) + "." + parameter.getName()));
  }

  private String getParamType(String in) {
    return in.equals("query") ? "querystring" : in;
  }

  private Map<String, Object> getResponseParameters() {
    Map<String, Object> responseParameters = swaggerProperties
        .getXAmazonApigatewayCors()
        .getCorsResponseParameters();
    RESPONSE_PARAMETERS.forEach(parameter -> responseParameters.put(
        M_RESP_H_PREFIX + parameter,
        I_RESP_H_PREFIX + parameter));

    return HandledHttpStatus.getList().stream()
        .collect(Collectors.toMap(
            httpStatus -> String.valueOf(httpStatus.value()),
            httpStatus -> Map.of(
                "responseParameters",
                responseParameters,
                "statusCode",
                String.valueOf(httpStatus.value()))));
  }

  private Map<String, SecuritySchemeDefinition> getSecurityDefinitions() {
    return swaggerProperties.getSecurityDefinitions().stream()
        .collect(Collectors.toMap(
            SecurityDefinitionProperties::getDefinitionName,
            SecurityDefinitionProperties::getApiKeyAuthDefinition));
  }
}
