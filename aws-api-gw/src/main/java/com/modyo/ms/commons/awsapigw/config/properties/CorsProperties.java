package com.modyo.ms.commons.awsapigw.config.properties;

import static com.modyo.ms.commons.awsapigw.constants.AwsExtensionsPrefixes.M_RESP_H_PREFIX;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.modyo.ms.commons.http.constants.CustomHttpHeaders;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CorsProperties {

  @JsonIgnore
  private Boolean enableGeneralConfiguration = false;

  @JsonIgnore
  private Boolean enableMockOptionMethods = false;

  private List<String> allowOrigins = List.of("*");

  private List<String> exposeHeaders = List.of(
      HttpHeaders.CONTENT_DISPOSITION,
      CustomHttpHeaders.APPLICATION_NAME,
      CustomHttpHeaders.CORRELATION_ID);

  private Integer maxAge = 3600;

  private List<String> allowMethods = List.of(
      HttpMethod.GET.name(),
      HttpMethod.POST.name(),
      HttpMethod.PUT.name(),
      HttpMethod.PATCH.name(),
      HttpMethod.DELETE.name(),
      HttpMethod.OPTIONS.name());

  private List<String> allowHeaders = List.of(
      HttpHeaders.ORIGIN,
      HttpHeaders.AUTHORIZATION,
      HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
      HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
      "X-Requested-With",
      "X-Amz-Date",
      "X-Amz-Security-Token",
      "X-Api-Key",
      "X-Captcha-Response");

  private Boolean allowCredentials;

  @JsonIgnore
  public Map<String, Object> getCorsResponseParameters() {
    Map<String, Object> responseParameters =  new HashMap<>();
//    responseParameters.put(
//        M_RESP_H_PREFIX + HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
//        "'" + String.join(",", getAllowOrigins()) + "'");
//    responseParameters.put(
//        M_RESP_H_PREFIX + HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
//        "'" + String.join(",", getAllowMethods()) + "'");
//    responseParameters.put(
//        M_RESP_H_PREFIX + HttpHeaders.ACCESS_CONTROL_MAX_AGE,
//        "'" + getMaxAge() + "'");
    responseParameters.put(
        M_RESP_H_PREFIX + HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
        "'" + String.join(",", getExposeHeaders()) + "'");
//    if (allowCredentials != null && allowCredentials) {
//      responseParameters.put(
//          M_RESP_H_PREFIX + HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS,
//          "'" + getAllowCredentials() + "'");
//    }
    return responseParameters;
  }

}
