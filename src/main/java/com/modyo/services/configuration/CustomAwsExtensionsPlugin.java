package com.modyo.services.configuration;

import static com.google.common.base.Strings.isNullOrEmpty;

import com.modyo.services.configuration.dto.CustomAwsDto;
import com.google.common.base.Optional;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.ArrayList;
import java.util.Arrays;
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
import springfox.documentation.service.ObjectVendorExtension;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;


/**
 * Esta clase es un plugin para Spring Fox que permite agregar los tags necesarios al Swagger. Para que se puede importar
 * automaticamente al API Gateway y tener una plantilla CloudFormation mas liviana
 */
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
@Slf4j
public class CustomAwsExtensionsPlugin implements OperationBuilderPlugin {

  // Integration
  private static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION = "x-amazon-apigateway-integration";
  private static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE = "type";
  private static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION_CONNECTION_ID = "connectionId";
  private static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION_CONNECTION_TYPE = "connectionType";
  private static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD = "httpMethod";
  private static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION_CONTENTHANDLING = "contentHandling";
  private static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI = "uri";
  private static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION_REQUEST_PARAMS = "requestParameters";
  private static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION_REQUEST_RESPONSES = "responses";
  private static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION_REQUEST_RESPONSES_PARAMETERS = "responseParameters";
  private static final String AWS_X_AMAZON_APIGATEWAY_INTEGRATION_REQUEST_TEMPLATE = "requestTemplates";

  // Constantes para mantener el orden
  private static final String VPC_LINK = "VPC_LINK";
  private static final String HTTP = "http";
  private static final String MOCK = "mock";
  private static final String STATUS_CODE = "statusCode";
  private static final String CONVERT_TO_BINARY = "CONVERT_TO_BINARY";
  @Value("${aws.apigateway.baseUri}")
  private String awsApiGwBase;
  @Value("${aws.apigateway.connectionId}")
  private String connectionId;
  @Value("${server.port}")
  private Integer port;
  @Autowired
  private Environment env;
  @Autowired
  private List<HttpStatus> handledHttpStatusList;

  /**
   * Metodo principal que va aplicando los tags necesarios.
   *
   * @param context el contexto
   */
  @Override
  public void apply(OperationContext context) {
    Optional<ApiOperation> apiOperationOperational = context.findAnnotation(ApiOperation.class);
    if (apiOperationOperational.isPresent()) {
      ApiOperation apiOperation = apiOperationOperational.get();

      List<VendorExtension> extensions = new ArrayList<VendorExtension>();
      String tipoBinario = tipoBinario(context);
      // Si es OPTIONS flujo diferente
      if (apiOperation.httpMethod().equalsIgnoreCase(RequestMethod.OPTIONS.name())) {
        ObjectVendorExtension extension = processOptions(context, apiOperation, tipoBinario);
        extensions.add(extension);
        context.operationBuilder().extensions(extensions);
        return;
      }

      ObjectVendorExtension extension = processApiOperation(context, apiOperation);
      ObjectVendorExtension requestParameters = new ObjectVendorExtension(
        AWS_X_AMAZON_APIGATEWAY_INTEGRATION_REQUEST_PARAMS);

      processApiParamAnnotation(requestParameters, context);
      processApiImplicitParamsAnnotation(context, requestParameters);

      ObjectVendorExtension awsResponses = new ObjectVendorExtension(
        AWS_X_AMAZON_APIGATEWAY_INTEGRATION_REQUEST_RESPONSES);

      processApiResponses(extension, awsResponses, tipoBinario);

      if (requestParameters != null && requestParameters.getValue() != null
        && requestParameters.getValue().size() > 0) {
        extension.addProperty(requestParameters);
      }
      if (awsResponses != null && awsResponses.getValue() != null
        && awsResponses.getValue().size() > 0) {
        extension.addProperty(awsResponses);
      }

      extensions.add(extension);

      context.operationBuilder().extensions(extensions);
    }
  }

  /**
   * Obtener Tipo Binario
   */
  private String tipoBinario(OperationContext context) {
    RequestMapping requestMapping = null;
    Optional<RequestMapping> requestMappingOptional = context
      .findAnnotation(RequestMapping.class);
    if (requestMappingOptional.isPresent()) {
      requestMapping = requestMappingOptional.get();
      String[] listProduces = requestMapping.produces();
      for (String s : listProduces) {
        // TODO: Manejar este listado igual en el plugin
        if ((s.equalsIgnoreCase(MediaType.APPLICATION_OCTET_STREAM_VALUE)) || (
          s.equalsIgnoreCase((MediaType.APPLICATION_PDF_VALUE))) || (
          s.equalsIgnoreCase(MediaType.IMAGE_PNG_VALUE)) || (
          s.equalsIgnoreCase(MediaType.IMAGE_GIF_VALUE)) || (
          s.equalsIgnoreCase(MediaType.IMAGE_JPEG_VALUE))) {
          return s;
        }
      }
    }
    if (context.getReturnType().getTypeName().contains("byte")) {
      return context.getReturnType().getTypeName();
    }

    return null;
  }


  /**
   * Encapsular la logica de las operaciones.
   *
   * @param context el contexto
   * @param apiOperation la operacion
   * @return el objeto vendor con las extensiones
   */
  private ObjectVendorExtension processOptions(OperationContext context,
    ApiOperation apiOperation, String tipoBinario) {
    ObjectVendorExtension extension = new ObjectVendorExtension(
      ensurePrefixed(AWS_X_AMAZON_APIGATEWAY_INTEGRATION));

    extension.addProperty(
      new StringVendorExtension(AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD,
        RequestMethod.OPTIONS.name()));
    ObjectVendorExtension awsResponses = new ObjectVendorExtension(
      AWS_X_AMAZON_APIGATEWAY_INTEGRATION_REQUEST_RESPONSES);
    ObjectVendorExtension awsResponse = new ObjectVendorExtension("200");
    awsResponse
      .addProperty(new StringVendorExtension(STATUS_CODE, "200"));

    extension.addProperty(awsResponses);

    ObjectVendorExtension responseParameters = new ObjectVendorExtension(
      AWS_X_AMAZON_APIGATEWAY_INTEGRATION_REQUEST_RESPONSES_PARAMETERS);
    responseParameters.addProperty(
      new StringVendorExtension("method.response.header.Access-Control-Allow-Methods",
        "'GET,POST,PUT,PATCH,DELETE,OPTIONS'"));
    responseParameters.addProperty(
      new StringVendorExtension("method.response.header.Access-Control-Allow-Headers",
        "'Access-Control-Allow-Headers,"
          + "Origin,"
          + "Accept,"
          + "X-Requested-With,"
          + "Content-Type,"
          + "Access-Control-Request-Method,"
          + "Access-Control-Request-Headers,"
          + "Authorization,"
          + "X-Amz-Date,"
          + "X-Api-Key,"
          + "X-Amz-Security-Token,"
          + "Vary,"
          + "captcha-response'"
      )
    );
    responseParameters.addProperty(
      new StringVendorExtension("method.response.header.Access-Control-Allow-Origin", "'*'"));
    responseParameters.addProperty(
      new StringVendorExtension("method.response.header.Vary", "'Origin'"));

    awsResponse.addProperty(responseParameters);
    awsResponses.addProperty(awsResponse);

    ObjectVendorExtension requestTemplate = new ObjectVendorExtension(
      AWS_X_AMAZON_APIGATEWAY_INTEGRATION_REQUEST_TEMPLATE);

    requestTemplate.addProperty(
      new StringVendorExtension(MediaType.APPLICATION_JSON_VALUE, "{'statusCode': 200}"));

    extension.addProperty(requestTemplate);

    extension
      .addProperty(new StringVendorExtension(AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE, MOCK));
    extension
      .addProperty(new StringVendorExtension("passthroughBehavior", "when_no_match"));

    return extension;
  }

  /**
   * Encapsular la logica de las operaciones.
   *
   * @param context el contexto
   * @param apiOperation la operacion
   * @return el objeto vendor con las extensiones
   */
  private ObjectVendorExtension processApiOperation(OperationContext context,
    ApiOperation apiOperation) {
    ObjectVendorExtension extension = new ObjectVendorExtension(
      ensurePrefixed(AWS_X_AMAZON_APIGATEWAY_INTEGRATION));

    extension
      .addProperty(new StringVendorExtension(AWS_X_AMAZON_APIGATEWAY_INTEGRATION_TYPE, HTTP));
    extension.addProperty(
      new StringVendorExtension(AWS_X_AMAZON_APIGATEWAY_INTEGRATION_CONNECTION_ID, connectionId));

    CustomAwsDto customAwsDto = getRequestUri(context);
    if (apiOperation.httpMethod() != null && !apiOperation.httpMethod().equals("")) {
      customAwsDto.setHttpMethod(apiOperation.httpMethod());
    }

    String url = awsApiGwBase + ":" + port + customAwsDto.getContextUri();
    url = url.replaceAll("(?<!(http:|https:))[//]+", "/");

    extension.addProperty(new StringVendorExtension(AWS_X_AMAZON_APIGATEWAY_INTEGRATION_URI, url));
    extension.addProperty(new StringVendorExtension(AWS_X_AMAZON_APIGATEWAY_INTEGRATION_HTTPMETHOD,
      customAwsDto.getHttpMethod()));
    extension.addProperty(
      new StringVendorExtension(AWS_X_AMAZON_APIGATEWAY_INTEGRATION_CONNECTION_TYPE, VPC_LINK));

    return extension;
  }

  /**
   * Este metodo permite agregar en Runtime los paths que vienen en el RequestMapping.
   *
   * @param context el contexto
   */
  private CustomAwsDto getRequestUri(OperationContext context) {
    CustomAwsDto customAwsDto = new CustomAwsDto();
    try {
      RequestMapping requestMapping = null;
      Optional<RequestMapping> requestMappingOptional = context
        .findAnnotation(RequestMapping.class);
      if (requestMappingOptional.isPresent()) {
        requestMapping = requestMappingOptional.get();
        RequestMethod[] methods = requestMapping.method();
        if (methods.length > 0) {
          customAwsDto.setHttpMethod(methods[0].name());
        }

        Optional<RequestMapping> controllerMappingOptional = context
          .findControllerAnnotation(RequestMapping.class);

        if (controllerMappingOptional.isPresent()) {
          RequestMapping controllerRequestMapping = controllerMappingOptional.get();
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

  /**
   * Metodo privado para reducir complejidad
   *
   * @param requestPaths los paths del metodo
   * @param paths los paths del controller
   */
  private String getRequestUriInternal(String[] requestPaths, String[] paths) {
    String requestUri = "";
    if (requestPaths != null && requestPaths.length > 0) {
      if (requestPaths[0].startsWith("$")) {
        String path = env.getProperty(eliminarCaracteresExpresion(requestPaths[0]));
        requestUri = "/" + path + "/";
      } else {
        requestUri = "/" + requestPaths[0] + "/";
      }

      if (paths != null && paths.length > 0) {
        if (paths[0].startsWith("$")) {
          String path2 = env.getProperty(eliminarCaracteresExpresion(paths[0]));
          requestUri = requestUri + "/" + path2;
        } else {
          requestUri = requestUri + "/" + paths[0];
        }
      }
    }
    return requestUri;
  }

  /**
   * Procesar las anotaciones ApiResponses.
   *
   * @param awsResponses las respuestas
   */
  private void processApiResponses(ObjectVendorExtension extension,
    ObjectVendorExtension awsResponses, String tipoBinario) {
    ObjectVendorExtension responseParameters = new ObjectVendorExtension(
      AWS_X_AMAZON_APIGATEWAY_INTEGRATION_REQUEST_RESPONSES_PARAMETERS);

    responseParameters.addProperty(
      new StringVendorExtension("method.response.header.Access-Control-Allow-Origin",
        "'*'"));

    for (HttpStatus status : handledHttpStatusList) {
      ObjectVendorExtension awsResponse = new ObjectVendorExtension(
        String.valueOf(status.value()));
      awsResponse
        .addProperty(
          new StringVendorExtension(STATUS_CODE, String.valueOf(status.value())));
      awsResponses.addProperty(awsResponse);
      awsResponse.addProperty(responseParameters);
    }

    ObjectVendorExtension awsResponse = new ObjectVendorExtension(
      "200");
    awsResponse
      .addProperty(
        new StringVendorExtension(STATUS_CODE, "200"));
    if (tipoBinario != null) {
      extension.addProperty(
        new StringVendorExtension(AWS_X_AMAZON_APIGATEWAY_INTEGRATION_CONTENTHANDLING,
          CONVERT_TO_BINARY));
      awsResponse
        .addProperty(
          new StringVendorExtension(AWS_X_AMAZON_APIGATEWAY_INTEGRATION_CONTENTHANDLING,
            CONVERT_TO_BINARY));
    }
    // Copiamos los headers de la respuesta
    responseParameters.addProperty(
      new StringVendorExtension("method.response.header.Vary", "'Origin'"));
    responseParameters.addProperty(
      new StringVendorExtension("method.response.header.Content-Disposition",
        "integration.response.header.Content-Disposition"));
    responseParameters.addProperty(
      new StringVendorExtension("method.response.header.Content-Length",
        "integration.response.header.Content-Length"));
    responseParameters.addProperty(
      new StringVendorExtension("method.response.header.Content-Type",
        "integration.response.header.Content-Type"));
    // custom headers
    responseParameters.addProperty(
      new StringVendorExtension("method.response.header.X-Application-Name",
        "integration.response.header.X-Application-Name"));
    responseParameters.addProperty(
      new StringVendorExtension("method.response.header.X-Parents-Correlation-Ids",
        "integration.response.header.X-Parents-Correlation-Ids"));
    responseParameters.addProperty(
      new StringVendorExtension("method.response.header.X-Correlation-ID",
        "integration.response.header.X-Correlation-ID"));

    awsResponse.addProperty(responseParameters);
    awsResponses.addProperty(awsResponse);
  }

  /**
   * Procesar las Anotaciones de Api Implicita para agregar el Authorization.
   *
   * @param context el contexto
   * @param requestParameters los parametros
   */
  private void processApiImplicitParamsAnnotation(OperationContext context,
    ObjectVendorExtension requestParameters) {

    Optional<ApiImplicitParams> globalApiImplicitParams = context
      .findAnnotation(ApiImplicitParams.class);

    List<ApiImplicitParam> apiImplicitParams = new ArrayList<>();

    if (globalApiImplicitParams.isPresent()) {
      ApiImplicitParam[] apiImplicitParamsArray = globalApiImplicitParams.get().value();
      apiImplicitParams = Arrays.asList(apiImplicitParamsArray);
    } else {
      apiImplicitParams = context.findAllAnnotations(ApiImplicitParam.class);
    }

    if (!apiImplicitParams.isEmpty()) {
      String requestParameterIntegration = "";
      String requestParameterMethod = "";
      for (ApiImplicitParam apiImplicitParam : apiImplicitParams) {
        if (apiImplicitParam.paramType() != null && apiImplicitParam.paramType().equals("header")) {
          requestParameterIntegration = "integration.request.header." + apiImplicitParam.name();
          requestParameterMethod = "method.request.header." + apiImplicitParam.name();
          requestParameters.addProperty(
            new StringVendorExtension(requestParameterIntegration, requestParameterMethod));
        } else if (apiImplicitParam.paramType() != null && apiImplicitParam.paramType()
          .equals("query")) {
          requestParameterIntegration =
            "integration.request.querystring." + apiImplicitParam.name();
          requestParameterMethod = "method.request.querystring." + apiImplicitParam.name();
          requestParameters.addProperty(
            new StringVendorExtension(requestParameterIntegration, requestParameterMethod));
        }
      }
    }
  }

  /**
   * Agregamos PathVariable y RequestParam al Swagger en formato API GW.
   *
   * @param requestParameters Los parametros
   * @param context el contexto de operacion
   */
  private void processApiParamAnnotation(ObjectVendorExtension requestParameters,
    OperationContext context) {
    List<ResolvedMethodParameter> methodParameters = context.getParameters();

    for (ResolvedMethodParameter methodParameter : methodParameters) {
      Optional<ApiParam> apiParam = methodParameter.findAnnotation(ApiParam.class);
      if (apiParam.isPresent()) {
        String requestParameterIntegration = "";
        String requestParameterMethod = "";

        Optional<PathVariable> pathVariable = methodParameter.findAnnotation(PathVariable.class);
        Optional<RequestParam> requestParam = methodParameter.findAnnotation(RequestParam.class);
        if (pathVariable.isPresent()) {
          requestParameterIntegration = "integration.request.path." + apiParam.get().name();
          requestParameterMethod = "method.request.path." + apiParam.get().name();
          requestParameters.addProperty(
            new StringVendorExtension(requestParameterIntegration, requestParameterMethod));
        } else if (requestParam.isPresent()) {
          requestParameterIntegration = "integration.request.querystring." + apiParam.get().name();
          requestParameterMethod = "method.request.querystring." + apiParam.get().name();
          requestParameters.addProperty(
            new StringVendorExtension(requestParameterIntegration, requestParameterMethod));
        }
      }
    }
  }

  /**
   * Nos aseguramos de agregar el tag Correcto.
   *
   * @param name el nombre
   * @return el string con el prefijo
   */
  private String ensurePrefixed(String name) {
    if ((!isNullOrEmpty(name)) && (!name.startsWith("x-"))) {
      name = "x-" + name;
    }
    return name;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }

  /**
   * Elimina caracteres propios de la expresion Spring.
   *
   * @param expresion La expresion
   * @return el string sin los caracteres
   */
  public String eliminarCaracteresExpresion(String expresion) {
    return expresion.replace("$", "").replace("{", "").replace("}", "");
  }
}
