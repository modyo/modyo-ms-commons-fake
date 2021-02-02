package com.modyo.ms.commons.awsapigw.components;

import static com.modyo.ms.commons.awsapigw.constants.AwsExtensionsPrefixes.M_RESP_H_PREFIX;

import com.modyo.ms.commons.awsapigw.config.properties.ApiGwSwaggerProperties;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.StringProperty;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

@RequiredArgsConstructor
@Component
public class OptionsMockOperationBuilder {

  private final ApiGwSwaggerProperties swaggerProperties;

  public Operation buildOptionsOperation(Path path) {
    Operation operation = new Operation();
    operation.tag(findTag(path));
    operation.summary("Options method");
    operation.operationId(findTag(path).replace(" ", "") + "OPTIONS");
    operation.consumes(MediaType.APPLICATION_JSON_VALUE);
    operation.produces(MediaType.APPLICATION_JSON_VALUE);
    Response response = buildOptionsResponse();
    operation.setResponses(Map.of(
        String.valueOf(HttpStatus.OK.value()),
        response));
    operation.deprecated(false);

    operation.vendorExtensions(Map.of(
        "x-amazon-apigateway-integration",
        Map.of(
            "httpMethod",
            RequestMethod.OPTIONS.name(),
            "passthroughBehavior",
            "when_no_match",
            "type",
            "mock",
            "requestTemplates",
            Map.of(
                MediaType.APPLICATION_JSON_VALUE,
                "{'statusCode': 200}"),
            "responses",
            Map.of(
                String.valueOf(HttpStatus.OK.value()),
                buildApiGwIntegrationOptionsResponse()))
    ));
    return operation;
  }

  private String findTag(Path path) {
    return path.getOperations().get(0)
        .getTags()
        .get(0);
  }

  private Response buildOptionsResponse() {
    Response optionsResponse = new Response();
    optionsResponse.description("OK");
    optionsResponse.headers(Map.of(
        HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
        new StringProperty(),
        HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
        new StringProperty(),
        HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
        new StringProperty(),
        HttpHeaders.ACCESS_CONTROL_MAX_AGE,
        new IntegerProperty(),
        HttpHeaders.VARY,
        new StringProperty()));
    return optionsResponse;
  }

  private Map<String, Object> buildApiGwIntegrationOptionsResponse() {
    return Map.of(
        "responseParameters",
        Map.of(
            M_RESP_H_PREFIX + HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
            "'*'",
            M_RESP_H_PREFIX + HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
            "'" + String.join(",", swaggerProperties.getXAmazonApigatewayCors().getAllowHeaders()) + "'",
            M_RESP_H_PREFIX + HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
            "'" + String.join(",", swaggerProperties.getXAmazonApigatewayCors().getAllowMethods()) + "'",
            M_RESP_H_PREFIX + HttpHeaders.ACCESS_CONTROL_MAX_AGE,
            "'" + swaggerProperties.getXAmazonApigatewayCors().getMaxAge() + "'",
            M_RESP_H_PREFIX + HttpHeaders.VARY,
            "'Origin'"
        ),
        "statusCode",
        String.valueOf(HttpStatus.OK.value())
    );
  }

}
