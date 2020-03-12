package com.modyo.ms.commons.awsapigw.components;

import com.google.common.base.Optional;
import com.modyo.ms.commons.core.constants.HandledHttpStatus;
import com.modyo.ms.commons.awsapigw.aspects.RequiresLambdaAuthorization;
import com.modyo.ms.commons.awsapigw.dtos.CustomAwsDto;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.service.ListVendorExtension;
import springfox.documentation.service.ObjectVendorExtension;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/**
 * Plugin de Spring Fox que permite agregar los tags necesarios al Swagger para el registro automático de los métodos en el API
 * Gateway
 */
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
@Slf4j
public class CustomAwsExtensionsPlugin implements OperationBuilderPlugin {

  @Value("${aws.apigateway.baseUri}")
  private String awsApiGwBase;
  @Value("${aws.apigateway.connectionId}")
  private String connectionId;
  @Value("${aws.apigateway.authorizerName}")
  private String authorizerName;
  @Value("${server.port}")
  private Integer port;
  @Autowired
  private Environment env;

  private static final String[] ACCESS_CONTROL_ALLOWED_METHODS = {
    "GET",
    "POST",
    "PUT",
    "PATCH",
    "DELETE",
    "OPTIONS"
  };

  private static final String[] ACCESS_CONTROL_ALLOWED_HEADERS = {
    "Access-Control-Allow-Headers",
    "Origin",
    "Accept",
    "X-Requested-With",
    "Content-Type",
    "Access-Control-Request-Method",
    "Access-Control-Request-Headers",
    "Authorization",
    "X-Amz-Date",
    "X-Api-Key",
    "X-Amz-Security-Token",
    "Vary",
    "captcha-response",
    "X-Captcha-Response"
  };

  private static HashMap<String, String> responseParameters;

  static {
    responseParameters = new HashMap<>();
    responseParameters.put("Access-Control-Allow-Methods", "'" + String.join(",", ACCESS_CONTROL_ALLOWED_METHODS) + "'");
    responseParameters.put("Access-Control-Allow-Headers", "'" + String.join(",", ACCESS_CONTROL_ALLOWED_HEADERS) + "'");
    responseParameters.put("Access-Control-Allow-Origin", "'*'");
    responseParameters.put("Vary", "'Origin'");
  }

  private static final String[] RESPONSE_HEADERS = {
    "Content-Disposition",
    "Content-Length",
    "Content-Type",
    "X-Application-Name",
    "X-Parents-Correlation-Ids",
    "X-Correlation-ID"
  };

  @Override
  public void apply(OperationContext context) {
    Optional<ApiOperation> apiOperationAnnotation = context.findAnnotation(ApiOperation.class);
    if (apiOperationAnnotation.isPresent()) {
      ApiOperation apiOperation = apiOperationAnnotation.get();
      List<VendorExtension> extensions = new ArrayList<>();
      if (apiOperation.httpMethod().equalsIgnoreCase(RequestMethod.OPTIONS.name())) {
        extensions.add(getAwsApiGwIntegrationExtForOptions());
      } else {
        extensions.add(getAwsApiGwIntegrationExt(context, apiOperation));
      }
      addSecurityExtensionIfNeeded(context, extensions);
      context.operationBuilder().extensions(extensions);
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }

  private void addSecurityExtensionIfNeeded(OperationContext context, List<VendorExtension> extensions) {
    Optional<RequiresLambdaAuthorization> lambdaAuthAnnotation = context.findAnnotation(RequiresLambdaAuthorization.class);
    if (lambdaAuthAnnotation.isPresent()) {
      HashMap<String, Object> map = new HashMap<>();
      map.put(authorizerName, Collections.emptyList());
      ListVendorExtension<HashMap> securityExt = new ListVendorExtension<>("security", Collections.singletonList(map));
      extensions.add(securityExt);
    }
  }

  private ObjectVendorExtension getAwsApiGwIntegrationExt(OperationContext context, ApiOperation apiOperation) {
    ObjectVendorExtension extension = processApiOperation(context, apiOperation);
    ObjectVendorExtension requestParameters = new ObjectVendorExtension("requestParameters");
    processApiParamAnnotation(requestParameters, context);
    processApiImplicitParamsAnnotation(context, requestParameters);
    ObjectVendorExtension awsResponses = new ObjectVendorExtension("responses");
    processApiResponses(extension, awsResponses, isBinaryType(context));
    if (!requestParameters.getValue().isEmpty()) {
      extension.addProperty(requestParameters);
    }
    if (!awsResponses.getValue().isEmpty()) {
      extension.addProperty(awsResponses);
    }
    return extension;
  }

  private boolean isBinaryType(OperationContext context) {
    String typeName = context.getReturnType().getTypeName();
    return typeName.contains("byte");
  }

  private ObjectVendorExtension getAwsApiGwIntegrationExtForOptions() {
    ObjectVendorExtension awsApiGwIntegrationExt = new ObjectVendorExtension("x-amazon-apigateway-integration");
    awsApiGwIntegrationExt.addProperty(new StringVendorExtension("httpMethod", RequestMethod.OPTIONS.name()));
    awsApiGwIntegrationExt.addProperty(new StringVendorExtension("passthroughBehavior", "when_no_match"));
    awsApiGwIntegrationExt.addProperty(new StringVendorExtension("type", "mock"));
    awsApiGwIntegrationExt.addProperty(getRequestTemplatesExtension());
    awsApiGwIntegrationExt.addProperty(getResponsesExtension());
    return awsApiGwIntegrationExt;
  }

  private ObjectVendorExtension getRequestTemplatesExtension() {
    ObjectVendorExtension requestTemplatesExt = new ObjectVendorExtension("requestTemplates");
    requestTemplatesExt.addProperty(new StringVendorExtension(MediaType.APPLICATION_JSON_VALUE, "{'statusCode': 200}"));
    return requestTemplatesExt;
  }

  private ObjectVendorExtension getResponsesExtension() {
    ObjectVendorExtension responsesExt = new ObjectVendorExtension("responses");
    ObjectVendorExtension successResponseExt = new ObjectVendorExtension(String.valueOf(HttpStatus.OK.value()));
    successResponseExt.addProperty(getResponseParametersExtension());
    successResponseExt.addProperty(getSuccessStatusCode());
    responsesExt.addProperty(successResponseExt);
    return responsesExt;
  }

  private ObjectVendorExtension getResponseParametersExtension() {
    ObjectVendorExtension responseParametersExt = new ObjectVendorExtension("responseParameters");
    responseParameters.forEach((k, v) ->
      responseParametersExt.addProperty(new StringVendorExtension("method.response.header." + k, v))
    );
    return responseParametersExt;
  }

  private ObjectVendorExtension processApiOperation(OperationContext context, ApiOperation apiOperation) {
    ObjectVendorExtension extension = new ObjectVendorExtension("x-amazon-apigateway-integration");
    extension.addProperty(new StringVendorExtension("type", "http"));
    extension.addProperty(new StringVendorExtension("connectionId", connectionId));
    CustomAwsDto customAwsDto = getRequestUri(context);
    if (!apiOperation.httpMethod().isEmpty()) {
      customAwsDto.setHttpMethod(apiOperation.httpMethod());
    }
    String url = awsApiGwBase + ":" + port + customAwsDto.getContextUri();
    url = url.replaceAll("(?<!(http:|https:))[//]+", "/");
    extension.addProperty(new StringVendorExtension("uri", url));
    extension.addProperty(new StringVendorExtension("httpMethod", customAwsDto.getHttpMethod()));
    extension.addProperty(new StringVendorExtension("connectionType", "VPC_LINK"));
    return extension;
  }

  private CustomAwsDto getRequestUri(OperationContext context) {
    CustomAwsDto customAwsDto = new CustomAwsDto();
    try {
      RequestMapping requestMapping;
      Optional<RequestMapping> requestMappingAnnotation = context.findAnnotation(RequestMapping.class);
      if (requestMappingAnnotation.isPresent()) {
        requestMapping = requestMappingAnnotation.get();
        RequestMethod[] methods = requestMapping.method();
        if (methods.length > 0) {
          customAwsDto.setHttpMethod(methods[0].name());
        }
        Optional<RequestMapping> controllerMappingAnnotation = context.findControllerAnnotation(RequestMapping.class);
        if (controllerMappingAnnotation.isPresent()) {
          RequestMapping controllerRequestMapping = controllerMappingAnnotation.get();
          String[] paths = requestMapping.path();
          String[] requestPaths = controllerRequestMapping.value();
          String requestUri = getRequestUriInternal(requestPaths, paths);
          customAwsDto.setContextUri(requestUri);
        }
      }
      return customAwsDto;
    } catch (Exception ex) {
      ex.printStackTrace();
      return customAwsDto;
    }
  }

  private String getRequestUriInternal(String[] requestPaths, String[] paths) {
    String requestUri = "";
    if (requestPaths != null && requestPaths.length > 0) {
      if (requestPaths[0].startsWith("$")) {
        String path = env.getProperty(formatExpresion(requestPaths[0]));
        requestUri = "/" + path + "/";
      } else {
        requestUri = "/" + requestPaths[0] + "/";
      }

      if (paths != null && paths.length > 0) {
        if (paths[0].startsWith("$")) {
          String path2 = env.getProperty(formatExpresion(paths[0]));
          requestUri = requestUri + "/" + path2;
        } else {
          requestUri = requestUri + "/" + paths[0];
        }
      }
    }
    return requestUri;
  }

  private void processApiResponses(ObjectVendorExtension extension, ObjectVendorExtension awsResponses, boolean binaryType) {
    //TODO revisar este método que al parecer está mal implementado
    ObjectVendorExtension responseParametersExt = new ObjectVendorExtension("responseParameters");
    responseParametersExt.addProperty(new StringVendorExtension("method.response.header.Access-Control-Allow-Origin", "'*'"));
    responseParametersExt.addProperty(new StringVendorExtension(
      "method.response.header.Access-Control-Expose-Headers",
      "'" + String.join(",", RESPONSE_HEADERS) + "'"
    ));
    HandledHttpStatus.getList().forEach(status -> {
      ObjectVendorExtension awsResponse = new ObjectVendorExtension(String.valueOf(status.value()));
      awsResponse.addProperty(new StringVendorExtension("statusCode", String.valueOf(status.value())));
      awsResponses.addProperty(awsResponse);
      awsResponse.addProperty(responseParametersExt);
    });

    ObjectVendorExtension awsResponse = new ObjectVendorExtension(String.valueOf(HttpStatus.OK.value()));
    awsResponse.addProperty(getSuccessStatusCode());
    if (binaryType) {
      StringVendorExtension contentHandlingExt = new StringVendorExtension("contentHandling", "CONVERT_TO_BINARY");
      extension.addProperty(contentHandlingExt);
      awsResponse.addProperty(contentHandlingExt);
    }
    Arrays.stream(RESPONSE_HEADERS).forEach(header -> responseParametersExt.addProperty(new StringVendorExtension(
      "method.response.header." + header, "integration.response.header." + header
    )));
    awsResponse.addProperty(responseParametersExt);
    awsResponses.addProperty(awsResponse);
  }

  private void processApiImplicitParamsAnnotation(OperationContext context, ObjectVendorExtension requestParameters) {
    Optional<ApiImplicitParams> globalApiImplicitParams = context.findAnnotation(ApiImplicitParams.class);
    List<ApiImplicitParam> apiImplicitParams;
    if (globalApiImplicitParams.isPresent()) {
      ApiImplicitParam[] apiImplicitParamsArray = globalApiImplicitParams.get().value();
      apiImplicitParams = Arrays.asList(apiImplicitParamsArray);
    } else {
      apiImplicitParams = context.findAllAnnotations(ApiImplicitParam.class);
    }
    if (!apiImplicitParams.isEmpty()) {
      String requestParameterIntegration;
      String requestParameterMethod;
      for (ApiImplicitParam apiImplicitParam : apiImplicitParams) {
        if (apiImplicitParam.paramType().equals("header")) {
          requestParameterIntegration = "integration.request.header." + apiImplicitParam.name();
          requestParameterMethod = "method.request.header." + apiImplicitParam.name();
          requestParameters.addProperty(
            new StringVendorExtension(requestParameterIntegration, requestParameterMethod));
        } else if (apiImplicitParam.paramType().equals("query")) {
          requestParameterIntegration =
            "integration.request.querystring." + apiImplicitParam.name();
          requestParameterMethod = "method.request.querystring." + apiImplicitParam.name();
          requestParameters.addProperty(
            new StringVendorExtension(requestParameterIntegration, requestParameterMethod));
        }
      }
    }
  }

  private void processApiParamAnnotation(ObjectVendorExtension requestParameters, OperationContext context) {
    List<ResolvedMethodParameter> methodParameters = context.getParameters();
    methodParameters.forEach(methodParameter -> {
      Optional<ApiParam> apiParam = methodParameter.findAnnotation(ApiParam.class);
      if (apiParam.isPresent()) {
        String requestParameterIntegration;
        String requestParameterMethod;
        Optional<PathVariable> pathVariableAnnotation = methodParameter.findAnnotation(PathVariable.class);
        Optional<RequestParam> requestParamAnnotation = methodParameter.findAnnotation(RequestParam.class);
        if (pathVariableAnnotation.isPresent()) {
          requestParameterIntegration = "integration.request.path." + apiParam.get().name();
          requestParameterMethod = "method.request.path." + apiParam.get().name();
          requestParameters.addProperty(new StringVendorExtension(requestParameterIntegration, requestParameterMethod));
        } else if (requestParamAnnotation.isPresent()) {
          requestParameterIntegration = "integration.request.querystring." + apiParam.get().name();
          requestParameterMethod = "method.request.querystring." + apiParam.get().name();
          requestParameters.addProperty(new StringVendorExtension(requestParameterIntegration, requestParameterMethod));
        }
      }
    });
  }

  private StringVendorExtension getSuccessStatusCode() {
    return new StringVendorExtension("statusCode", String.valueOf(HttpStatus.OK.value()));
  }

  private String formatExpresion(String expresion) {
    return expresion
      .replace("$", "")
      .replace("{", "")
      .replace("}", "");
  }
}
