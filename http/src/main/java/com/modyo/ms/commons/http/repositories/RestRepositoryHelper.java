package com.modyo.ms.commons.http.repositories;

import com.modyo.ms.commons.http.config.properties.RestMethodProperties;
import com.modyo.ms.commons.http.config.properties.RestWebServiceProperties;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Setter
public class RestRepositoryHelper {
  private static final Boolean DEFAULT_URI_ENCODED = true;

  public static <T> ResponseEntity<T> executeRequest(
      RestTemplate restTemplate,
      RestWebServiceProperties webServiceProperties,
      String method,
      RequestParams requestParams,
      Boolean uriEncoded,
      Class<T> responseType
  ) {
    RestMethodProperties methodProperties = webServiceProperties.getMethods().get(method);
    return restTemplate.exchange(
        buildUri(webServiceProperties, methodProperties, requestParams, uriEncoded),
        methodProperties.getHttpMethod(),
        new HttpEntity<>(requestParams.getBody(),
            buildHeaders(webServiceProperties, methodProperties, requestParams.getHeaders())),
        responseType);
  }

  public static <T> ResponseEntity<T> executeRequest(
      RestTemplate restTemplate,
      RestWebServiceProperties webServiceProperties,
      String method,
      RequestParams requestParams,
      Class<T> responseType
  ) {
    return executeRequest(
        restTemplate,
        webServiceProperties,
        method,
        requestParams,
        DEFAULT_URI_ENCODED,
        responseType);
  }

  public static <T> ResponseEntity<T> executeRequest(
      RestTemplate restTemplate,
      RestWebServiceProperties webServiceProperties,
      String method,
      Class<T> responseType
  ) {
    return executeRequest(
        restTemplate,
        webServiceProperties,
        method,
        emptyRequestParams(),
        DEFAULT_URI_ENCODED,
        responseType);
  }

  public static <T> ResponseEntity<T> executeRequest(
      RestTemplate restTemplate,
      RestWebServiceProperties webServiceProperties,
      String method, Boolean uriEncoded,
      Class<T> responseType
  ) {
    return executeRequest(
        restTemplate,
        webServiceProperties,
        method,
        emptyRequestParams(),
        uriEncoded,
        responseType);
  }

  private static RequestParams emptyRequestParams() {
    return RequestParams.builder().build();
  }

  private static String buildUri(
      RestWebServiceProperties webServiceProperties,
      RestMethodProperties methodProperties,
      RequestParams requestParams,
      Boolean uriEncoded) {

    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(
        webServiceProperties.getBaseUrl() + methodProperties.getPath());
    Optional.ofNullable(requestParams.getPaths())
        .ifPresent(pathList -> pathList.forEach(uriBuilder::pathSegment));
    Optional.ofNullable(methodProperties.getQueryParams())
        .ifPresent(queryParamsMap -> queryParamsMap.forEach(uriBuilder::queryParam));
    Optional.ofNullable(requestParams.getQueryParams())
        .ifPresent(queryParamsMap -> queryParamsMap.forEach(uriBuilder::queryParam));

    return uriBuilder.build(uriEncoded).toUriString();
  }

  private static HttpHeaders buildHeaders(
      RestWebServiceProperties webServiceProperties,
      RestMethodProperties methodProperties,
      HttpHeaders dynamicHeaders) {

    HttpHeaders httpHeaders = new HttpHeaders();

    Optional.ofNullable(webServiceProperties.getBasicAuth())
        .ifPresent(basicAuth -> httpHeaders.setBasicAuth(
            basicAuth.getUsername(),
            basicAuth.getPassword()));
    Optional.ofNullable(webServiceProperties.getBearerAuth()).ifPresent(httpHeaders::setBearerAuth);
    Optional.ofNullable(webServiceProperties.getHeaders()).ifPresent(httpHeaders::putAll);
    Optional.ofNullable(methodProperties.getHeaders()).ifPresent(httpHeaders::putAll);
    Optional.ofNullable(dynamicHeaders).ifPresent(httpHeaders::putAll);

    return httpHeaders;
  }

  @Builder
  @Getter
  public static class RequestParams {

    private HttpHeaders headers;
    private List<String> paths;
    private Map<String, String> queryParams;
    private Object body;

  }

}
