package com.modyo.ms.commons.http.repositories;

import com.modyo.ms.commons.http.config.properties.RestMethodProperties;
import com.modyo.ms.commons.http.config.properties.RestWebServiceProperties;
import java.io.Serializable;
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
public class RestRepository {

  public <T> ResponseEntity<T> executeRequest(
      RestTemplate restTemplate,
      RestWebServiceProperties webServiceProperties,
      String method,
      RequestParams requestParams,
      Class <T> responseType
  ){
    RestMethodProperties methodProperties = webServiceProperties.getMethods().get(method);
    return restTemplate.exchange(
        buildUri(webServiceProperties, methodProperties, requestParams),
        methodProperties.getHttpMethod(),
        new HttpEntity<>(
            requestParams.getBody(),
            buildHeaders(webServiceProperties, requestParams.getHeaders())),
        responseType);
  }

  private String buildUri(
      RestWebServiceProperties webServiceProperties,
      RestMethodProperties methodProperties,
      RequestParams requestParams) {

    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(
        webServiceProperties.getBaseUrl() + methodProperties.getPath());
    Optional.ofNullable(requestParams.getPaths())
        .ifPresent(pathList -> pathList.forEach(uriBuilder::pathSegment));
    Optional.ofNullable(requestParams.getQueryParams())
        .ifPresent(queryParamsMap -> queryParamsMap.forEach(uriBuilder::queryParam));

    return uriBuilder.toUriString();
  }

  private HttpHeaders buildHeaders(
      RestWebServiceProperties webServiceProperties,
      HttpHeaders dynamicHeaders) {

    HttpHeaders httpHeaders = new HttpHeaders();

    Optional.ofNullable(webServiceProperties.getBasicAuth())
        .ifPresent(basicAuth -> httpHeaders.setBasicAuth(
            basicAuth.getUsername(),
            basicAuth.getPassword()));
    Optional.ofNullable(webServiceProperties.getBearerAuth()).ifPresent(httpHeaders::setBearerAuth);
    Optional.ofNullable(webServiceProperties.getHeaders()).ifPresent(httpHeaders::putAll);
    Optional.ofNullable(dynamicHeaders).ifPresent(httpHeaders::putAll);

    return httpHeaders;
  }

  @Builder
  @Getter
  public static class RequestParams {

    private HttpHeaders headers;
    private List<String> paths;
    private Map<String, String> queryParams;
    private Serializable body;

  }

}
