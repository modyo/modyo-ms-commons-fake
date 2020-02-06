package com.modyo.services.interceptors;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import springfox.documentation.spring.web.json.Json;

/**
 * Advice para modificar la salida de Swagger y sacar los caracteres con problemas al importar a AWS API GATEWAY
 */
@ControllerAdvice
public class SwaggerAwsAdvice implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(MethodParameter returnType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(Object body, MethodParameter returnType,
      MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request, ServerHttpResponse response) {
    try {
      if (body instanceof Json) {
        Json ret = (Json) body;
        String retorno = ret.value().replaceAll("\\u00ab", "").replaceAll("\\u00bb", "");
        Json rr = new Json(retorno);
        return rr;
      }
      return body;
    } catch (Exception ex) {
      return body;
    }
  }
}
